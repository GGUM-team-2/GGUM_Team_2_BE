package com.example.ggum.domain.post.repository;

import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.status.PostType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByPostType(PostType postType);
}
