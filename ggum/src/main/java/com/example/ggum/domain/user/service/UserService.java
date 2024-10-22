package com.example.ggum.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User create(final User user) {
        if(user ==null || user.getEmail()==null) {
            throw new RuntimeException("invalid arguments");
        }
        final String email= user.getEmail();
        if(userRepository.existsByEmail(email)) {
            log.warn("Email already exists {}",email);
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }

    public User getByCredentials(final String email, final String password, final PasswordEncoder encoder) {
        final User originalUser=userRepository.findByEmail(email);
        if(originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }
}
