package com.cloud6.place.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud6.place.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // username으로 사용자 조회

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
