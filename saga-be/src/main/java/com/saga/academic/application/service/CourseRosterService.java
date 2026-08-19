package com.saga.academic.application.service;

import com.saga.academic.infrastructure.persistence.entity.ActiveSemesterSettingEntity;
import com.saga.academic.infrastructure.persistence.entity.CourseEntity;
import com.saga.academic.infrastructure.persistence.entity.TeamEntity;
import com.saga.academic.infrastructure.persistence.entity.TeamMemberEntity;
import com.saga.academic.infrastructure.persistence.repository.JpaActiveSemesterRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaCourseRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamMemberRepository;
import com.saga.academic.infrastructure.persistence.repository.JpaTeamRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.DataFormatter;

@Service
public class CourseRosterService {
    private final JpaCourseRepository courseRepository;
    private final JpaActiveSemesterRepository activeSemesterRepository;
    private final JpaTeamRepository teamRepository;
    private final JpaTeamMemberRepository teamMemberRepository;

    public CourseRosterService(JpaCourseRepository courseRepository,
            JpaActiveSemesterRepository activeSemesterRepository, JpaTeamRepository teamRepository,
            JpaTeamMemberRepository teamMemberRepository) {
        this.courseRepository = courseRepository;
        this.activeSemesterRepository = activeSemesterRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    private CourseEntity getCourseAndAuthorize(UUID courseId, UUID lecturerId) {
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getInstructorId().equals(lecturerId)) {
            throw new AccessDeniedException("You are not authorized to manage this course's roster");
        }
        return course;
    }

    private void validateActiveSemester(CourseEntity course) {
        List<ActiveSemesterSettingEntity> settings = activeSemesterRepository.findAll();
        if (settings.isEmpty() || !settings.get(0).getSemesterId().equals(course.getSemesterId())) {
            throw new IllegalArgumentException("This course does not belong to the active semester");
        }
    }

    public byte[] downloadGroupingTemplate(UUID courseId, UUID lecturerId) {
        getCourseAndAuthorize(courseId, lecturerId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Team Grouping");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student UUID");
            headerRow.createCell(1).setCellValue("Team Name");
            headerRow.createCell(2).setCellValue("Is Leader (TRUE/FALSE)");
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating template", e);
        }
    }

    @Transactional
    public void importTeamGrouping(UUID courseId, UUID lecturerId, MultipartFile file) {
        CourseEntity course = getCourseAndAuthorize(courseId, lecturerId);
        validateActiveSemester(course);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                DataFormatter formatter = new DataFormatter();
                String studentIdStr = formatter.formatCellValue(row.getCell(0));
                String teamName = formatter.formatCellValue(row.getCell(1));
                boolean isLeader = Boolean.parseBoolean(formatter.formatCellValue(row.getCell(2)));

                TeamEntity team = teamRepository.findByNameAndCourseId(teamName, courseId).orElseGet(() -> {
                    TeamEntity newTeam = new TeamEntity();
                    newTeam.setCourseId(courseId);
                    newTeam.setName(teamName);
                    return teamRepository.save(newTeam);
                });

                TeamMemberEntity member = new TeamMemberEntity();
                member.setTeamId(team.getId());
                member.setStudentId(UUID.fromString(studentIdStr));
                member.setIsLeader(isLeader);
                teamMemberRepository.save(member);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing file", e);
        }
    }
}