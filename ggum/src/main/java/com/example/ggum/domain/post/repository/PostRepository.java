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
}
