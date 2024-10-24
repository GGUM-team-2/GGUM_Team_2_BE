package com.example.ggum.domain.post.service;

import com.example.ggum.domain.post.converter.PostConverter;
import com.example.ggum.domain.post.dto.PostRequestDTO;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.repository.PostLikeRepository;
import com.example.ggum.domain.post.repository.PostRepostiroy;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepostiroy postRepostiroy;

    private final PostLikeRepository postLikeRepository;

    @Autowired
    private UserRepository userRepository;// UserRepository 주입

    @Override
    @Transactional
    public Post postCreate(PostRequestDTO.PostCreateDto request, Long userId){
        // 사용자 정보를 가져오기 위한 리포지토리 호출
        User user = userRepository.findById(userId);
        Post post = PostConverter.toPost(request,user);
        return postRepostiroy.save(post);
    }
}
