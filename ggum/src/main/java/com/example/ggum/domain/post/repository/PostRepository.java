package com.example.ggum.domain.post.repository;

import com.example.ggum.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}