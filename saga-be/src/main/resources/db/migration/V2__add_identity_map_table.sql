CREATE TABLE identity_map (
    id UUID PRIMARY KEY,
    internal_user_id UUID NOT NULL,
    external_provider VARCHAR(50) NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_identity_user FOREIGN KEY (internal_user_id) REFERENCES users(id)
);