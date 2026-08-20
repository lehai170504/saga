package com.saga.academic.service;

import com.saga.academic.entity.ActiveSemesterSetting;
import com.saga.academic.entity.Course;
import com.saga.academic.entity.Team;
import com.saga.academic.entity.TeamMember;
import com.saga.academic.entity.CourseStudent;
import com.saga.academic.repository.JpaActiveSemesterRepository;
import com.saga.academic.repository.JpaCourseRepository;
import com.saga.academic.repository.JpaTeamMemberRepository;
import com.saga.academic.repository.JpaTeamRepository;
import com.saga.academic.repository.JpaCourseStudentRepository;
import com.saga.user.repository.JpaUserRepository;
import com.saga.user.entity.User;
import com.saga.user.entity.Role;
import com.saga.user.entity.UserStatus;
import com.saga.shared.service.EmailService;
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
import java.util.Optional;

@Service
public class CourseRosterService {
    private final JpaCourseRepository courseRepository;
    private final JpaActiveSemesterRepository activeSemesterRepository;
    private final JpaTeamRepository teamRepository;
    private final JpaTeamMemberRepository teamMemberRepository;
    private final JpaCourseStudentRepository courseStudentRepository;
    private final JpaUserRepository userRepository;
    private final EmailService emailService;

    public CourseRosterService(JpaCourseRepository courseRepository,
            JpaActiveSemesterRepository activeSemesterRepository, JpaTeamRepository teamRepository,
            JpaTeamMemberRepository teamMemberRepository, JpaCourseStudentRepository courseStudentRepository,
            JpaUserRepository userRepository, EmailService emailService) {
        this.courseRepository = courseRepository;
        this.activeSemesterRepository = activeSemesterRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.courseStudentRepository = courseStudentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private Course getCourse(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    private Course getCourseAndAuthorize(UUID courseId, UUID lecturerId) {
        Course course = getCourse(courseId);
        if (!course.getInstructorId().equals(lecturerId)) {
            throw new AccessDeniedException("You are not authorized to manage this course's roster");
        }
        return course;
    }

    private void validateActiveSemester(Course course) {
        List<ActiveSemesterSetting> settings = activeSemesterRepository.findAll();
        if (settings.isEmpty() || !settings.get(0).getSemesterId().equals(course.getSemesterId())) {
            throw new IllegalArgumentException("This course does not belong to the active semester");
        }
    }

    // ==========================================
    // ADMIN: ROSTER MANAGEMENT
    // ==========================================

    public byte[] downloadRosterTemplate(UUID courseId) {
        getCourse(courseId); // validate exists
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Student Roster");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Email");
            headerRow.createCell(1).setCellValue("Full Name (Optional)");
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating template", e);
        }
    }

    @Transactional
    public void importRoster(UUID courseId, MultipartFile file) {
        Course course = getCourse(courseId);
        validateActiveSemester(course);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                DataFormatter formatter = new DataFormatter();
                String email = formatter.formatCellValue(row.getCell(0)).trim();
                String name = formatter.formatCellValue(row.getCell(1)).trim();

                if (email.isEmpty())
                    continue;

                // Create user if not exists
                Optional<User> existingUser = userRepository.findByEmail(email);
                User user;
                boolean isNewUser = false;
                if (existingUser.isEmpty()) {
                    user = User.builder()
                            .id(UUID.randomUUID())
                            .email(email)
                            .name(name.isEmpty() ? email.split("@")[0] : name)
                            .role(Role.STUDENT)
                            .status(UserStatus.PENDING)
                            .build();
                    user = userRepository.save(user);
                    isNewUser = true;
                } else {
                    user = existingUser.get();
                }

                // Add to course_students
                Optional<CourseStudent> existingCourseStudent = courseStudentRepository
                        .findByCourseIdAndStudentId(courseId, user.getId());
                if (existingCourseStudent.isEmpty()) {
                    CourseStudent cse = new CourseStudent();
                    cse.setCourseId(courseId);
                    cse.setStudentId(user.getId());
                    courseStudentRepository.save(cse);

                    // Send Email
                    emailService.sendCourseEnrollmentEmail(email, course.getId().toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing roster file", e);
        }
    }

    // ==========================================
    // LECTURER: TEAM GROUPING
    // ==========================================

    public byte[] downloadGroupingTemplate(UUID courseId, UUID lecturerId) {
        Course course = getCourseAndAuthorize(courseId, lecturerId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Team Grouping");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Email");
            headerRow.createCell(1).setCellValue("Full Name");
            headerRow.createCell(2).setCellValue("Team Name");
            headerRow.createCell(3).setCellValue("Is Leader (TRUE/FALSE)");

            // Pre-fill with enrolled students
            List<CourseStudent> courseStudents = courseStudentRepository.findAll().stream()
                    .filter(cs -> cs.getCourseId().equals(courseId)).toList();
            int rowIndex = 1;
            for (CourseStudent cs : courseStudents) {
                User student = userRepository.findById(cs.getStudentId()).orElse(null);
                if (student != null) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(student.getEmail());
                    row.createCell(1).setCellValue(student.getName());
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating template", e);
        }
    }

    @Transactional
    public void importTeamGrouping(UUID courseId, UUID lecturerId, MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        Course course = getCourseAndAuthorize(courseId, lecturerId);
        validateActiveSemester(course);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                DataFormatter formatter = new DataFormatter();
                String email = formatter.formatCellValue(row.getCell(0)).trim();
                String teamName = formatter.formatCellValue(row.getCell(2)).trim();
                boolean isLeader = Boolean.parseBoolean(formatter.formatCellValue(row.getCell(3)));

                if (email.isEmpty() || teamName.isEmpty())
                    continue;

                User student = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Student with email " + email + " not found"));

                Team team = teamRepository.findByNameAndCourseId(teamName, courseId).orElseGet(() -> {
                    Team newTeam = new Team();
                    newTeam.setCourseId(courseId);
                    newTeam.setName(teamName);
                    return teamRepository.save(newTeam);
                });

                // Check if already in a team
                TeamMember member = new TeamMember();
                member.setTeamId(team.getId());
                member.setStudentId(student.getId());
                member.setIsLeader(isLeader);
                teamMemberRepository.save(member);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing file", e);
        }
    }
}
