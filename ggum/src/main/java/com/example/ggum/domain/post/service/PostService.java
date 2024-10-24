package com.example.ggum.domain.post.service;

import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.dto.PostRequestDTO;

public interface PostService {
    public Post postCreate(PostRequestDTO.PostCreateDto request,Long userId);
    public void deletePost(Long boardId, Long userId);
}
