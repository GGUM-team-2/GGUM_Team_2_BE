package com.example.ggum.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ggum.domain.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String Username);
    User findByEmailAndPassword(String email, String password);

    User findById(Long id);

}
