package com.example.ggum.domain.post.controller;


import com.example.ggum.domain.post.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ggum.security.TokenProvider;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class PostLikeController {
    @Autowired
    private PostLikeService postLikeService;
    private final TokenProvider tokenProvider;

    @PostMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> likepost(
            @PathVariable("postId") Long postId,
            @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));

        Map<String, Object> response = postLikeService.togglePostLike(postId, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
