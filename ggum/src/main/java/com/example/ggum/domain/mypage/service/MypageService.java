package com.example.ggum.domain.mypage.service;

import com.example.ggum.domain.mypage.dto.MypageDTO;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.PostLike;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MypageService {

    private List<Post> posts; // Post 엔티티 리스트
    private List<PostLike> postLikes; // PostLike 엔티티 리스트

    // MypageService 생성자
    public MypageService(List<Post> posts, List<PostLike> postLikes) {
        this.posts = posts;
        this.postLikes = postLikes;
    }
    public MypageDTO getMypageData(Long userId) {
        MypageDTO mypageDTO = new MypageDTO();
        // 마이페이지 시본프로필 설정하는 기능..?? 기본 값으로 설정할 수 있음
        //mypageDTO.setProfileImage("default_profile_image.png");
        //mypageDTO.setId(userId); // 유저 ID 설정
        return mypageDTO;
    }


    // 1. user_id와 post_id가 일치하는 게시물 제목 불러오기
    public List<String> getPostInfoByUserId(Long userId) {
        return posts.stream()
                .filter(post -> post.getUser().getId().equals(userId)) // user_id가 post_id와 일치하는지 확인
                .map(Post::getTitle) // 일치하는 게시물의 제목만 가져오기
                .collect(Collectors.toList());
    }

    // 2. liked_id와 user_id가 일치하는 게시물 제목 불러오기
    public List<String> getLikedPostInfoByUserId(Long userId) {
        return postLikes.stream()
                .filter(postLike -> postLike.getUser().getId().equals(userId)) // user_id와 liked_id가 일치하는지 확인
                .map(postLike -> postLike.getPost().getTitle()) // 일치하는 게시물의 제목만 가져오기
                .collect(Collectors.toList());
    }
}
