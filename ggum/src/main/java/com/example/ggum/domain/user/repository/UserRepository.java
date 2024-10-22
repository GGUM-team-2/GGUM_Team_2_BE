package com.example.ggum.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ggum.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    User findByEmailAndPassword(String email, String password);
}
