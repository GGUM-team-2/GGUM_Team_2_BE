package com.example.ggum.domain.post.service;

import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.PostLike;
import com.example.ggum.domain.post.repository.PostLikeRepository;
import com.example.ggum.domain.post.repository.PostRepository;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public void createPostLike(Post post, User user) {

        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Long postId = post.getId();
        Long userId = user.getId();

        PostLike savedPostLike = postLikeRepository.save(postLike);

        Long postLikeId = savedPostLike.getId();

    }

    public void deletePostLike(Post post, User user) {
        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user,post);

        postLike.ifPresent(postLikeRepository::delete);

    }

    public Map<String, Object> togglePostLike(Long postId, Long userId) {

        User user = userRepository.findById(userId);
        Post post = postRepository.findById(postId).get();

        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user,post);

        if (postLike.isPresent()) {
            deletePostLike(post, user);
        } else {
            createPostLike(post, user);
        }
        return Map.of();
    }

}
