package com.saga.academic.service;

import com.saga.academic.dto.CourseRequest;
import com.saga.academic.dto.CourseResponse;
import com.saga.academic.entity.Course;
import com.saga.academic.repository.JpaClassRepository;
import com.saga.academic.repository.JpaCourseRepository;
import com.saga.academic.repository.JpaCourseStudentRepository;
import com.saga.academic.repository.JpaSemesterRepository;
import com.saga.academic.repository.JpaSubjectRepository;
import com.saga.shared.exception.BadRequestException;
import com.saga.user.repository.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CourseService {

    private final JpaCourseRepository courseRepository;
    private final JpaCourseStudentRepository courseStudentRepository;
    private final JpaSemesterRepository semesterRepository;
    private final JpaSubjectRepository subjectRepository;
    private final JpaClassRepository classRepository;
    private final JpaUserRepository userRepository;

    public CourseService(JpaCourseRepository courseRepository, JpaCourseStudentRepository courseStudentRepository,
            JpaSemesterRepository semesterRepository, JpaSubjectRepository subjectRepository,
            JpaClassRepository classRepository, JpaUserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.courseStudentRepository = courseStudentRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.classRepository = classRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @com.saga.shared.annotation.LogAction(actionType = "CREATE_COURSE")
    public CourseResponse createCourse(CourseRequest request) {
        if (!semesterRepository.existsById(request.getSemesterId()))
            throw new BadRequestException("Semester not found");
        if (!subjectRepository.existsById(request.getSubjectId()))
            throw new BadRequestException("Subject not found");
        if (!classRepository.existsById(request.getClassId()))
            throw new BadRequestException("Class not found");

        if (courseRepository.existsBySemesterIdAndSubjectIdAndClassId(
                request.getSemesterId(), request.getSubjectId(), request.getClassId())) {
            throw new BadRequestException("This Course combination (Semester, Subject, Class) already exists.");
        }

        Course course = new Course();
        course.setSemesterId(request.getSemesterId());
        course.setSubjectId(request.getSubjectId());
        course.setClassId(request.getClassId());
        course.setInstructorId(request.getInstructorId());

        Course saved = courseRepository.save(course);
        return mapToResponse(saved);
    }

    public Page<CourseResponse> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(this::mapToResponse);
    }

    public Page<CourseResponse> getLecturerCourses(UUID lecturerId, Pageable pageable) {
        return courseRepository.findByInstructorId(lecturerId, pageable).map(this::mapToResponse);
    }

    public Page<CourseResponse> getStudentCourses(UUID studentId, Pageable pageable) {
        return courseStudentRepository.findCoursesByStudentId(studentId, pageable).map(this::mapToResponse);
    }

    private CourseResponse mapToResponse(Course course) {
        String semesterName = semesterRepository.findById(course.getSemesterId()).map(s -> s.getName()).orElse(null);
        String subjectName = subjectRepository.findById(course.getSubjectId()).map(s -> s.getSubjectName())
                .orElse(null);
        String classCode = classRepository.findById(course.getClassId()).map(c -> c.getClassCode()).orElse(null);
        String instructorName = null;

        if (course.getInstructorId() != null) {
            instructorName = userRepository.findById(course.getInstructorId()).map(u -> u.getName()).orElse(null);
        }

        return CourseResponse.builder()
                .id(course.getId())
                .semesterId(course.getSemesterId())
                .semesterName(semesterName)
                .subjectId(course.getSubjectId())
                .subjectName(subjectName)
                .classId(course.getClassId())
                .classCode(classCode)
                .instructorId(course.getInstructorId())
                .instructorName(instructorName)
                .build();
    }
}
