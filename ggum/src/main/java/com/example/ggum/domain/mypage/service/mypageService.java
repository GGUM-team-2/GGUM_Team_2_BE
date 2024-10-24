package com.example.ggum.domain.mypage.service;

import com.example.ggum.dto.MypageDTO;
import com.example.ggum.dto.PostDTO;
import com.example.ggum.repository.MypageRepository;
import com.example.ggum.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MypageService {

    @Autowired
    private MypageRepository mypageRepository;

    @Autowired
    private PostRepository postRepository;

    public MypageDTO getUserDetails(Long id) {
        Mypage user = mypageRepository.findById(id);
        MypageDTO dto = new MypageDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setProfileImage(user.getProfileImage());

        // 게시글 가져오기
        List<PostDTO> posts = postRepository.findByUserId(id).stream()
                .map(post -> {
                    PostDTO postDTO = new PostDTO();
                    postDTO.setPostId(post.getId());
                    postDTO.setTitle(post.getTitle());
                    postDTO.setContent(post.getContent());
                    postDTO.setStates(post.getStates());
                    return postDTO;
                })
                .collect(Collectors.toList());

        // 찜한 게시글 가져오기
        List<PostDTO> likedPosts = postRepository.findByLikedIsTrue().stream()
                .map(post -> {
                    PostDTO postDTO = new PostDTO();
                    postDTO.setPostId(post.getId());
                    postDTO.setTitle(post.getTitle());
                    postDTO.setContent(post.getContent());
                    postDTO.setStates(post.getStates());
                    return postDTO;
                })
                .collect(Collectors.toList());

        dto.setPosts(posts);
        dto.setLikedPosts(likedPosts);
        return dto;
    }
}