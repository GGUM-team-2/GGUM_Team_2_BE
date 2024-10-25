package com.example.ggum.domain.post.controller;

import com.example.ggum.domain.chat.entity.ChatRoom;
import com.example.ggum.domain.chat.repository.ChatRoomRepository;
import com.example.ggum.domain.chat.repository.JoinChatRepository;
import com.example.ggum.domain.chat.repository.MessageRepository;
import com.example.ggum.domain.post.converter.PostConverter;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ggum.domain.post.dto.PostResponseDTO;
import com.example.ggum.domain.post.dto.PostRequestDTO;

import com.example.ggum.security.TokenProvider;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Tag(name = "Post API", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;
    private final TokenProvider tokenProvider;

    @PostMapping("/")
    @Operation(summary = "게시글 생성", description = "게시글 생성")
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
    @Operation(summary = "게시글 삭제", description = "게시글 삭제")
    public ResponseEntity<String> deletePost(
            @PathVariable("postId") Long postId,
            @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
        postService.deletePost(postId, userId);

        return ResponseEntity.ok("success");
    }


    @PatchMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "해당 게시글의 모든 값을 함께 넘김")
    public ResponseEntity<PostResponseDTO.PostResultDTO> updatePost(
            @PathVariable("postId") Long postId,
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
    @Operation(summary = "하나의 게시글 조회", description = "게시글 id를 받고 그 게시글을 조회")
    public ResponseEntity<PostResponseDTO.ReadPostDTO> readOnePost(
            @PathVariable("PostId") Long postId,
            @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
        Post post = postService.readOnePost(postId, userId);

        return ResponseEntity.ok(PostConverter.toReadPostDTO(post));
    }

    @GetMapping("/")
    @Operation(summary = "모든 게시글 조회", description = "모든 게시글 필터와 status를 이용해 조회")
    public ResponseEntity<PostResponseDTO.ReadPostListDTO> readPost(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page, // 기본값 0
            @RequestParam(value = "size", defaultValue = "10") int size, // 기본값 10
            @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
        PostResponseDTO.ReadPostListDTO response = postService.readPost(filter, status, page, size);

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{postId}/status")
    @Operation(summary = "게시글 상태 또는 참여인원수 변경", description = "게시글 상태 또는 참여 인원수 변경, 둘 중 하나만 넣어도 가능")
    public ResponseEntity<PostResponseDTO.ReadPostDTO> updatePost(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "participantCount", required = false) Long participantCount,
            @RequestParam(value = "postStatus", required = false) PostStatus postStatus,
            @RequestHeader("Authorization") String token) {

        // 서비스 메소드를 통해 게시글 업데이트
        Post updatedPost = postService.updatePost(postId, participantCount, postStatus);
        System.out.println(updatedPost);
        if (updatedPost == null) {
            return ResponseEntity.notFound().build(); // 게시글이 존재하지 않으면 에러
        }

        // ReadPostDTO로 변환
        PostResponseDTO.ReadPostDTO readPostDTO = PostConverter.toReadPostDTO(updatedPost);

        return ResponseEntity.ok(readPostDTO);
    }



}
