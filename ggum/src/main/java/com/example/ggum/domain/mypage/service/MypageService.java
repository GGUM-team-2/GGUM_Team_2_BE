package com.example.ggum.domain.mypage.service;

import com.example.ggum.domain.mypage.dto.MypageDTO;
import com.example.ggum.domain.post.converter.PostConverter;
import com.example.ggum.domain.post.dto.PostResponseDTO;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.PostLike;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.entity.status.PostType;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.ggum.domain.post.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MypageService {

    private final List<Post> posts;
    private final List<PostLike> postLikes;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public MypageService(List<Post> posts, List<PostLike> postLikes, UserRepository userRepository, PostRepository postRepository) {
        this.posts = posts;
        this.postLikes = postLikes;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public PostResponseDTO.ReadPostListDTO readMyPost(Long userId, PostType filter, int page, int size) {
        // 내림차순 정렬 설정 (post_id 기준)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Post> postPage;

        // userId로만 전체 게시글 조회하고 filter를 적용
        if (userId != null) {
            if (filter == null) {
                // filter가 null일 때 userId로 전체 게시물 조회
                postPage = postRepository.findByUserId(userId, pageable);
            } else {
                // filter가 있을 때 해당 필터 적용하여 게시물 조회
                switch (filter) {
                    case GROUP_PURCHASE: // 공동구매
                        postPage = postRepository.findByUserIdAndPostType(userId, PostType.GROUP_PURCHASE, pageable);
                        break;
                    case SHARING: // 나눔
                        postPage = postRepository.findByUserIdAndPostType(userId, PostType.SHARING, pageable);
                        break;
                    default: // 그 외
                        postPage = postRepository.findByUserId(userId, pageable);
                        break;
                }
            }
        } else {
            throw new IllegalArgumentException("잘못된 사용자 요청입니다."); // 예외 처리
        }

        List<PostResponseDTO.ReadPostDTO> postDTOs = postPage.getContent().stream()
                .map(PostConverter::toReadPostDTO)
                .collect(Collectors.toList());

        return new PostResponseDTO.ReadPostListDTO(postDTOs, (int) postPage.getTotalElements(), postPage.getNumber(), postPage.getTotalPages());
    }


    /*
    public MypageDTO getMypageData(Long userId) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId));
        /*if (userOptional.isPresent()) {
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
    */


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
