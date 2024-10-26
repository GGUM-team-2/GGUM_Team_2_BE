package com.example.ggum.domain.post.service;

import com.example.ggum.domain.post.dto.PostResponseDTO;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.dto.PostRequestDTO;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.entity.status.PostType;

import java.util.List;

public interface PostService {


    public Post postCreate(PostRequestDTO.PostCreateDto request,Long userId);
    public void deletePost(Long postId, Long userId);
    public Post postUpdate(Long postId, PostRequestDTO.PostUpdateDto request,Long userId);
    public Post readOnePost(Long postId);
    public PostResponseDTO.ReadPostListDTO readPost(PostType filter, PostStatus status, int page, int size);

    public Post updatePost(Long postId, Long participantCount, PostStatus postStatus);
}
