package com.saga.application.port;

import com.saga.domain.Student;

public interface StudentRepositoryPort {
    Student save(Student student);
}
