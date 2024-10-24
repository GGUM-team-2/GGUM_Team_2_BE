package com.example.ggum.domain.post.converter;

import com.example.ggum.domain.post.dto.PostRequestDTO;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.dto.PostResponseDTO;
import com.example.ggum.domain.post.entity.status.PostCategory;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class PostConverter {
    
    
    public static PostResponseDTO.PostResultDTO toJoinResultDTO(Post Post){

        return PostResponseDTO.PostResultDTO.builder()
                .postId(Post.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Post toPost(PostRequestDTO.PostCreateDto request, User user){

        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postCategory(PostMapper.toPostCategory(request.getCategory()))  // 카테고리 변환
                .postStatus(PostStatus.OPEN)
                .price(request.getPrice())
                .participantCount(1L)
                .participantLimit(request.getParticipant_limit())
                .postType(PostMapper.toPostType(request.getPostType()))
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

    }
}
