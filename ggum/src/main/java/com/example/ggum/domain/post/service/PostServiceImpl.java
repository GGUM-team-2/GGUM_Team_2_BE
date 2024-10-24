package com.example.ggum.domain.post.service;

import com.example.ggum.domain.post.converter.PostConverter;
import com.example.ggum.domain.post.converter.PostMapper;
import com.example.ggum.domain.post.dto.PostRequestDTO;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.repository.PostLikeRepository;
import com.example.ggum.domain.post.repository.PostRepository;
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

    private final PostRepository postRepository;

    private final PostLikeRepository postLikeRepository;

    @Autowired
    private UserRepository userRepository;// UserRepository 주입

    @Override
    @Transactional
    public Post postCreate(PostRequestDTO.PostCreateDto request, Long userId){
        // 사용자 정보를 가져오기 위한 리포지토리 호출
        User user = userRepository.findById(userId);
        Post post = PostConverter.toPost(request,user);
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다.")); // 게시물이 없을 경우 예외 처리

        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("게시물 소유자가 아닙니다."); // 소유자가 아닐 경우 예외 처리
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public Post postUpdate(Long postId, PostRequestDTO.PostUpdateDto request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다.")); // 게시물이 없을 경우 예외 처리

        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("게시물 소유자가 아닙니다."); // 소유자가 아닐 경우 예외 처리
        }

        // 요청 DTO를 사용하여 게시물 업데이트
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPrice(request.getPrice());
        post.setParticipantLimit(request.getParticipant_limit());
        post.setPostCategory(PostMapper.toPostCategory(request.getCategory()));
        post.setPostType(PostMapper.toPostType(request.getPostType()));

        return postRepository.save(post); // 업데이트된 게시물 반환
    }

}
