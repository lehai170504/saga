CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    picture VARCHAR(255),
    role VARCHAR(50),
    status VARCHAR(50)
);

CREATE TABLE students (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    student_code VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE lecturers (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL
);
