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

    public void withdraw(final Long userId) {
        if (userId == null) {
            throw new RuntimeException("Invalid user ID");
        }

        // 사용자 찾기
        final User user = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        log.info("Deleting user with ID: {}", userId);

        // 사용자 삭제
        userRepository.delete(user);
    }


    public User getByCredentials(final String email, final String password, final PasswordEncoder encoder) {
        final User originalUser=userRepository.findByEmail(email);
        if(originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }

    // 학교 웹메일인지 확인하는 파싱 알고리즘
    public boolean isSchoolEmail(String email) {
        boolean result = false;
        String[] parts = email.split("@");
        if (parts.length != 2) {// 이메일 형식인지 확인
            result = false;
        }
        else {//학교 웹 메일인지 확인
            String domain = parts[1];
            String[] domainParts = domain.split("\\.");
            int length = domainParts.length;
            if (length >= 3 && "ac".equals(domainParts[length - 2]) && "kr".equals(domainParts[length - 1])) {
                result = true;
            }
        }
        return result;
    }

    // 이메일 중복 확인 메서드
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 확인 메서드
    public boolean existsByUsername(String Username) {
        return userRepository.existsByUsername(Username);
    }
}
