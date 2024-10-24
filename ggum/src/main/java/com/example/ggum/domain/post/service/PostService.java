package com.example.ggum.domain.post.service;

import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.dto.PostRequestDTO;

public interface PostService {
    public Post postCreate(PostRequestDTO.PostCreateDto request,Long userId);
    public void deletePost(Long postId, Long userId);
    public Post postUpdate(Long postId, PostRequestDTO.PostUpdateDto request,Long userId);
    public Post readOnePost(Long postId, Long userId);
}
