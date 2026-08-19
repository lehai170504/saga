package com.saga.application.port;

import com.saga.domain.Lecturer;

public interface LecturerRepositoryPort {
    Lecturer save(Lecturer lecturer);
}
