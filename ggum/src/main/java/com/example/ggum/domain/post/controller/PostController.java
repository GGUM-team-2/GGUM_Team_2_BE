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
@RequestMapping("/api/v1/board")
public class PostController {

    private final PostService postService;
    private final TokenProvider tokenProvider;

    @PostMapping("/")
    public ApiResponse<PostResponseDTO.PostResultDTO> makePost(
            @RequestHeader("Authorization") String token, // Authorization 헤더에서 토큰을 받아옴
            @RequestBody @Valid PostRequestDTO.PostCreateDto request) {

        // "Bearer " 문자열을 제거하여 실제 토큰을 추출
        String jwtToken = token.replace("Bearer ", ""); // Bearer 부분 제거
        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(jwtToken)); // 토큰에서 사용자 ID 추출
        Post post = postService.postCreate(request, userId); // 사용자 ID를 서비스에 전달

        return ApiResponse.onSuccess(PostConverter.toJoinResultDTO(post));
    }




}
