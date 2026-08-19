package com.saga.user.application.port;

import com.saga.user.domain.Lecturer;

public interface LecturerRepositoryPort {
    Lecturer save(Lecturer lecturer);
}
