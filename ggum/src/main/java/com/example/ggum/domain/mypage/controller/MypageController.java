package com.example.ggum.domain.mypage.controller;

import com.example.ggum.domain.mypage.dto.MypageDTO;
import com.example.ggum.domain.mypage.service.MypageService;
import com.example.ggum.domain.post.dto.PostResponseDTO;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.entity.status.PostType;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/mypage")
public class MypageController {

    private final MypageService mypageService;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Autowired
    public MypageController(MypageService mypageService, UserRepository userRepository, TokenProvider tokenProvider) {
        this.mypageService = mypageService;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String userIdStr = extractUserIdFromToken(token);
            Long userId = Long.valueOf(userIdStr);

            Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserDTO userDTO = UserDTO.builder()
                        .username(user.getUsername())
                        .likeCount(user.getLikeCount())
                        .dislikeCount(user.getDislikeCount())
                        .build();
                return ResponseEntity.ok().body(userDTO);
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body("Token expired");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
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

    @PostMapping("/{userId}")
    public ResponseEntity<PostResponseDTO.ReadPostListDTO> getMypageData(@PathVariable("userId") Long userId,
                                                                         @RequestParam(value = "filter", required = false) PostType filter,
                                                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                                                        @RequestParam(value = "size", defaultValue = "10") int size)
    {

        PostResponseDTO.ReadPostListDTO mypageDTO= mypageService.readMyPost(userId, filter, page, size);
        return ResponseEntity.ok(mypageDTO);
    }

    private String extractUserIdFromToken(String token) {
        String actualToken = token.replace("Bearer ", "");
        return tokenProvider.validateAndGetUserId(actualToken);
    }

    @PostMapping("/liked-posts/{userId}")
    public ResponseEntity<List<String>> getLikedPosts(@PathVariable("userId") Long userId) {
        List<String> likedPostTitles = mypageService.getLikedPostTitlesByUserId(userId);
        return ResponseEntity.ok(likedPostTitles);
    }
}
