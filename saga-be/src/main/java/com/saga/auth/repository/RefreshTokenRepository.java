package com.saga.auth.repository;
import com.saga.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {}