package com.example.ggum.domain.post.service;

import com.example.ggum.domain.chat.entity.ChatRoom;
import com.example.ggum.domain.chat.repository.ChatRoomRepository;
import com.example.ggum.domain.chat.repository.JoinChatRepository;
import com.example.ggum.domain.chat.repository.MessageRepository;
import com.example.ggum.domain.post.converter.PostConverter;
import com.example.ggum.domain.post.converter.PostMapper;
import com.example.ggum.domain.post.dto.PostRequestDTO;
import com.example.ggum.domain.post.dto.PostResponseDTO;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.entity.status.PostType;
import com.example.ggum.domain.post.repository.PostLikeRepository;
import com.example.ggum.domain.post.repository.PostRepository;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final PostLikeRepository postLikeRepository;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JoinChatRepository joinChatRepository;
    private final UserRepository userRepository;// UserRepository 주입

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

        // 게시글에 속한 채팅방들을 모두 가져와서 삭제 진행
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByPostId(postId);

        for (ChatRoom chatRoom : chatRooms) {

            messageRepository.deleteByChatRoomId(chatRoom.getId());
            joinChatRepository.deleteByChatRoomId(chatRoom.getId());
            chatRoomRepository.deleteById(chatRoom.getId());
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

    @Override
    public Post readOnePost(Long postId, Long userId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다.")); // 게시물이 없을 경우 예외 처리

        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("게시물 소유자가 아닙니다."); // 소유자가 아닐 경우 예외 처리
        }

        return post;
    }

    @Override
    public PostResponseDTO.ReadPostListDTO readPost(String filter, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage;

        // 상태가 있을 때만 PostStatus로 변환
        PostStatus postStatus = (status != null) ? PostStatus.valueOf(status.toUpperCase()) : null;

        // 필터와 상태에 따라 다르게 쿼리 수행
        if (filter == null || filter.isEmpty()) {
            // filter가 null 또는 빈 문자열일 때
            if (postStatus != null) {
                postPage = postRepository.findByPostStatus(postStatus, pageable); // 상태만으로 필터링
            } else {
                postPage = postRepository.findAll(pageable); // 전체 가져오기
            }
        } else {
            switch (filter) {
                case "onlypurchase": // 공동구매
                    if (postStatus != null) {
                        postPage = postRepository.findByPostTypeAndPostStatus(PostType.GROUP_PURCHASE, postStatus, pageable);
                    } else {
                        postPage = postRepository.findByPostType(PostType.GROUP_PURCHASE, pageable); // 상태가 없으면 전체
                    }
                    break;
                case "onlysharing": // 나눔
                    if (postStatus != null) {
                        postPage = postRepository.findByPostTypeAndPostStatus(PostType.SHARING, postStatus, pageable);
                    } else {
                        postPage = postRepository.findByPostType(PostType.SHARING, pageable); // 상태가 없으면 전체
                    }
                    break;
                default: // "total" 또는 알 수 없는 경우
                    if (postStatus != null) {
                        postPage = postRepository.findByPostStatus(postStatus, pageable); // 상태만으로 필터링
                    } else {
                        postPage = postRepository.findAll(pageable); // 전체 가져오기
                    }
                    break;
            }
        }


        List<PostResponseDTO.ReadPostDTO> postDTOs = postPage.getContent().stream()
                .map(PostConverter::toReadPostDTO)
                .collect(Collectors.toList());

        return new PostResponseDTO.ReadPostListDTO(postDTOs, (int) postPage.getTotalElements(), postPage.getNumber(), postPage.getTotalPages());
    }



}
