package com.example.ggum.domain.post.repository;

import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.PostLike;
import com.example.ggum.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
}
