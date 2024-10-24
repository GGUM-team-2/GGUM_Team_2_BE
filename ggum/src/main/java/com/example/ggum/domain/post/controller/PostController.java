package com.example.ggum.domain.post.controller;

import com.example.ggum.domain.post.ApiResponse;
import com.example.ggum.domain.post.converter.PostConverter;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.service.PostService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<PostResponseDTO.PostResultDTO> makePost(
            @RequestHeader("Authorization") String token, // Authorization 헤더에서 토큰을 받아옴
            @RequestBody @Valid PostRequestDTO.PostCreateDto request) {

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token)); // 토큰에서 사용자 ID 추출
        Post post = postService.postCreate(request, userId); // 사용자 ID를 서비스에 전달

        return ApiResponse.onSuccess(PostConverter.toJoinResultDTO(post));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<String> deletePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
        postService.deletePost(postId, userId);

        return ApiResponse.onSuccess("success");
    }

    @PatchMapping("/{postId}")
    public ApiResponse<PostResponseDTO.PostResultDTO> updatePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid PostRequestDTO.PostUpdateDto request) {

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token)); // 토큰에서 사용자 ID 추출
        Post post = postService.postUpdate(postId, request, userId); // 사용자 ID를 서비스에 전달

        return ApiResponse.onSuccess(PostConverter.toJoinResultDTO(post));
    }


}
