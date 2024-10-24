package com.example.ggum.domain.post.controller;

import com.example.ggum.domain.post.converter.PostConverter;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ggum.domain.post.dto.PostResponseDTO;
import com.example.ggum.domain.post.dto.PostRequestDTO;

import com.example.ggum.security.TokenProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;
    private final TokenProvider tokenProvider;

    @PostMapping("/")
    public ResponseEntity<PostResponseDTO.PostResultDTO> makePost(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid PostRequestDTO.PostCreateDto request) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
        Post post = postService.postCreate(request, userId);

        return ResponseEntity.ok(PostConverter.toJoinResultDTO(post));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable() Long postId,
            @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
        postService.deletePost(postId, userId);

        return ResponseEntity.ok("success");
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDTO.PostResultDTO> updatePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid PostRequestDTO.PostUpdateDto request) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
        Post post = postService.postUpdate(postId, request, userId);

        return ResponseEntity.ok(PostConverter.toJoinResultDTO(post));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO.ReadPostDTO> readOnePost(
            @PathVariable() Long postId,
            @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
        Post post = postService.readOnePost(postId, userId);

        return ResponseEntity.ok(PostConverter.toReadPostDTO(post));
    }
}
