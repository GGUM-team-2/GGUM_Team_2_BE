package com.example.ggum.domain.user.controller;

import com.example.ggum.domain.user.dto.ResponseDTO;
import com.example.ggum.domain.user.dto.UserDTO;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import com.example.ggum.security.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/mypage")
public class MypageController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // "Bearer " 부분 제거하고 토큰만 추출
            String actualToken = token.replace("Bearer ", "");

            // 토큰에서 사용자 ID 추출
            String userIdStr = tokenProvider.validateAndGetUserId(actualToken);
            Long userId = Long.valueOf(userIdStr);

            // 사용자 정보 조회
            Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId));
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // 필요한 데이터만 UserDTO로 반환
                UserDTO userDTO = UserDTO.builder()
                        .username(user.getUsername())
                        .likeCount(user.getLikeCount())
                        .dislikeCount(user.getDislikeCount())
                        .build();

                return ResponseEntity.ok().body(userDTO);
            } else {
                return ResponseEntity.badRequest().body(new ResponseDTO("User not found", null));
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body(new ResponseDTO("Token expired", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseDTO("Internal server error", null));
        }
    }

    @PostMapping("/like")
    public ResponseEntity<?> incrementLikeCount(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String userIdStr = tokenProvider.validateAndGetUserId(actualToken);
            Long userId = Long.valueOf(userIdStr);

            Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setLikeCount(user.getLikeCount() + 1);
                userRepository.save(user);

                return ResponseEntity.ok().body(new ResponseDTO("Like count updated", Collections.singletonList(user.getLikeCount())));
            } else {
                return ResponseEntity.badRequest().body(new ResponseDTO("User not found", null));
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body(new ResponseDTO("Token expired", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseDTO("Internal server error", null));
        }
    }

    @PostMapping("/dislike")
    public ResponseEntity<?> incrementDislikeCount(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String userIdStr = tokenProvider.validateAndGetUserId(actualToken);
            Long userId = Long.valueOf(userIdStr);

            Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setDislikeCount(user.getDislikeCount() + 1);
                userRepository.save(user);

                return ResponseEntity.ok().body(new ResponseDTO("Dislike count updated", Collections.singletonList(user.getDislikeCount())));
            } else {
                return ResponseEntity.badRequest().body(new ResponseDTO("User not found", null));
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body(new ResponseDTO("Token expired", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseDTO("Internal server error", null));
        }
    }
}

