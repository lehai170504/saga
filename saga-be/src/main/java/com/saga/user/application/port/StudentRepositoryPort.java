package com.saga.user.application.port;

import com.saga.user.domain.Student;

public interface StudentRepositoryPort {
    Student save(Student student);
}
