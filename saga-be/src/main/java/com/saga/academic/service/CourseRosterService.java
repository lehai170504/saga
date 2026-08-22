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
import com.saga.academic.repository.JpaClassRepository;
import com.saga.academic.entity.Class;
import com.saga.user.repository.JpaStudentRepository;
import com.saga.user.entity.Student;

@Service
public class CourseRosterService {
    private final JpaCourseRepository courseRepository;
    private final JpaActiveSemesterRepository activeSemesterRepository;
    private final JpaTeamRepository teamRepository;
    private final JpaTeamMemberRepository teamMemberRepository;
    private final JpaCourseStudentRepository courseStudentRepository;
    private final JpaUserRepository userRepository;
    private final JpaStudentRepository studentRepository;
    private final EmailService emailService;
    private final JpaClassRepository classRepository;

    public CourseRosterService(JpaCourseRepository courseRepository,
            JpaActiveSemesterRepository activeSemesterRepository, JpaTeamRepository teamRepository,
            JpaTeamMemberRepository teamMemberRepository, JpaCourseStudentRepository courseStudentRepository,
            JpaUserRepository userRepository, JpaStudentRepository studentRepository,
            EmailService emailService, JpaClassRepository classRepository) {
        this.courseRepository = courseRepository;
        this.activeSemesterRepository = activeSemesterRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.courseStudentRepository = courseStudentRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
        this.classRepository = classRepository;
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
    @com.saga.shared.annotation.LogAction(actionType = "IMPORT_ROSTER")
    public void importRoster(UUID courseId, MultipartFile file) {
        Course course = getCourse(courseId);
        validateActiveSemester(course);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            java.util.Set<String> excelEmails = new java.util.HashSet<>();
            java.util.Set<UUID> processedStudentIds = new java.util.HashSet<>();

            // Phase 1: Collect emails and add missing students
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                DataFormatter formatter = new DataFormatter();
                String email = formatter.formatCellValue(row.getCell(0)).trim();
                String name = formatter.formatCellValue(row.getCell(1)).trim();

                if (email.isEmpty())
                    continue;

                excelEmails.add(email);

                Optional<User> existingUser = userRepository.findByEmail(email);
                User user;
                if (existingUser.isEmpty()) {
                    user = User.builder()
                            .id(UUID.randomUUID())
                            .email(email)
                            .name(name.isEmpty() ? email.split("@")[0] : name)
                            .role(Role.STUDENT)
                            .status(UserStatus.PENDING)
                            .build();
                    user = userRepository.save(user);

                    Student newStudent = Student.builder()
                            .id(UUID.randomUUID())
                            .userId(user.getId())
                            .studentCode(email.split("@")[0].toUpperCase())
                            .build();
                    studentRepository.save(newStudent);
                } else {
                    user = existingUser.get();
                }

                Optional<CourseStudent> existingCourseStudent = courseStudentRepository
                        .findByCourseIdAndStudentId(courseId, user.getId());
                if (existingCourseStudent.isEmpty()) {
                    CourseStudent cse = new CourseStudent();
                    cse.setCourseId(courseId);
                    cse.setStudentId(user.getId());
                    courseStudentRepository.save(cse);

                    String classCode = classRepository.findById(course.getClassId())
                            .map(Class::getClassCode)
                            .orElse(course.getId().toString());
                    emailService.sendCourseEnrollmentEmail(email, classCode);
                }

                processedStudentIds.add(user.getId());
            }

            // Phase 2: Remove students not in the excel
            List<CourseStudent> currentStudents = courseStudentRepository.findByCourseId(courseId);
            for (CourseStudent cs : currentStudents) {
                if (!processedStudentIds.contains(cs.getStudentId())) {
                    removeStudentFromCourse(cs.getStudentId(), courseId);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error processing roster file", e);
        }
    }

    @Transactional
    public void removeStudentFromCourse(UUID studentId, UUID courseId) {
        // 1. Remove from course_students
        courseStudentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .ifPresent(courseStudentRepository::delete);

        // 2. Remove from team members (Cascade)
        List<TeamMember> studentTeams = teamMemberRepository.findByStudentId(studentId);
        for (TeamMember tm : studentTeams) {
            Team team = teamRepository.findById(tm.getTeamId()).orElse(null);
            if (team != null && team.getCourseId().equals(courseId)) {
                // If they are leader, we just remove them and team becomes leaderless
                teamMemberRepository.delete(tm);
            }
        }
    }

    public byte[] downloadGroupingTemplate(UUID courseId, UUID lecturerId) {
        getCourseAndAuthorize(courseId, lecturerId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Team Grouping");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Email");
            headerRow.createCell(1).setCellValue("Full Name");
            headerRow.createCell(2).setCellValue("Team Name");
            headerRow.createCell(3).setCellValue("Is Leader (TRUE/FALSE)");

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

                Optional<TeamMember> existingMember = teamMemberRepository.findByTeamIdAndStudentId(team.getId(),
                        student.getId());
                if (existingMember.isEmpty()) {
                    TeamMember member = new TeamMember();
                    member.setTeamId(team.getId());
                    member.setStudentId(student.getId());
                    member.setIsLeader(isLeader);
                    teamMemberRepository.save(member);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing file", e);
        }
    }

    @Transactional
    public void addStudentToCourse(UUID courseId, String email) {
        Course course = getCourse(courseId);
        validateActiveSemester(course);

        email = email.trim();
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        if (existingUser.isEmpty()) {
            user = User.builder()
                    .id(UUID.randomUUID())
                    .email(email)
                    .name(email.split("@")[0])
                    .role(Role.STUDENT)
                    .status(UserStatus.PENDING)
                    .build();
            user = userRepository.save(user);

            Student newStudent = Student.builder()
                    .id(UUID.randomUUID())
                    .userId(user.getId())
                    .studentCode(email.split("@")[0].toUpperCase())
                    .build();
            studentRepository.save(newStudent);
        } else {
            user = existingUser.get();
        }

        Optional<CourseStudent> existingCourseStudent = courseStudentRepository
                .findByCourseIdAndStudentId(courseId, user.getId());
        if (existingCourseStudent.isEmpty()) {
            CourseStudent cse = new CourseStudent();
            cse.setCourseId(courseId);
            cse.setStudentId(user.getId());
            courseStudentRepository.save(cse);

            String classCode = classRepository.findById(course.getClassId())
                    .map(Class::getClassCode)
                    .orElse(course.getId().toString());
            emailService.sendCourseEnrollmentEmail(email, classCode);
        }
    }

    @Transactional
    public void updateTeamLeader(UUID courseId, UUID teamId, UUID newLeaderStudentId, UUID lecturerId) {
        getCourseAndAuthorize(courseId, lecturerId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        if (!team.getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("Team does not belong to this course");
        }

        // Verify the new leader is actually in the team
        TeamMember newLeader = teamMemberRepository.findByTeamIdAndStudentId(teamId, newLeaderStudentId)
                .orElseThrow(() -> new IllegalArgumentException("Student is not a member of this team"));

        // Remove old leader
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(teamId);
        for (TeamMember member : teamMembers) {
            if (member.getIsLeader()) {
                member.setIsLeader(false);
                teamMemberRepository.save(member);
            }
        }

        // Set new leader
        newLeader.setIsLeader(true);
        teamMemberRepository.save(newLeader);
    }
}
