package com.example.ggum.domain.mypage.service;

import com.example.ggum.domain.mypage.dto.MypageDTO;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.PostLike;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MypageService {

    private final List<Post> posts;
    private final List<PostLike> postLikes;
    private final UserRepository userRepository;

    public MypageService(List<Post> posts, List<PostLike> postLikes, UserRepository userRepository) {
        this.posts = posts;
        this.postLikes = postLikes;
        this.userRepository = userRepository;
    }

    public MypageDTO getMypageData(Long userId) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            MypageDTO mypageDTO = new MypageDTO();
            mypageDTO.setUserId(user.getId());
            mypageDTO.setTitle("User's Mypage");
            mypageDTO.setPostCategory("General"); // 기본 카테고리 설정
            mypageDTO.setPrice(0L); // 기본 가격 설정
            mypageDTO.setParticipantLimit(100L); // 기본 참가자 제한 설정
            mypageDTO.setParticipantCount((long) getPostInfoByUserId(userId).size());
            mypageDTO.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 설정
            return mypageDTO;
        }
        return null;
    }


    public List<String> getPostInfoByUserId(Long userId) {
        return posts.stream()
                .filter(post -> post.getUser().getId().equals(userId))
                .map(Post::getTitle)
                .collect(Collectors.toList());
    }

    public List<String> getLikedPostTitlesByUserId(Long userId) {
        return postLikes.stream()
                .filter(postLike -> postLike.getUser().getId().equals(userId))
                .map(postLike -> postLike.getPost().getTitle())
                .collect(Collectors.toList());
    }
}
