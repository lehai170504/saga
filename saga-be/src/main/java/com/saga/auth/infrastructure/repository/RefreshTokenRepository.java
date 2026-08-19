package com.saga.auth.infrastructure.repository;
import com.saga.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {}