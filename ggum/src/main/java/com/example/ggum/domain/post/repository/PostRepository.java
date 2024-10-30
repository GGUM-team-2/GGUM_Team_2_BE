package com.example.ggum.domain.post.repository;

import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.entity.status.PostType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByPostType(PostType postType, Pageable pageable);
    Page<Post> findByPostStatus(PostStatus postStatus, Pageable pageable);
    Page<Post> findByPostTypeAndPostStatus(PostType postType, PostStatus postStatus, Pageable pageable);
    // 특정 사용자(userId)와 게시물 타입(PostType)에 따라 게시물 조회
    Page<Post> findByUserIdAndPostType(Long userId, PostType postType, Pageable pageable);

    // 특정 사용자(userId)에 따라 모든 게시물 조회 (필터 없이)
    Page<Post> findByUserId(Long userId, Pageable pageable);
}
