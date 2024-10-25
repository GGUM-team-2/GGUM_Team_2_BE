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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        if(request.getParticipant_limit()<=0)
            throw new IllegalArgumentException("전체 참여 인원이 0 이하일 수 없습니다.");
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

        if(request.getParticipant_limit()<=0)
            throw new IllegalArgumentException("전체 참여 인원이 0 이하일 수 없습니다.");

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
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post); // 업데이트된 게시물 반환
    }

    @Override
    public Post readOnePost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다.")); // 게시물이 없을 경우 예외 처리

        return post;
    }

    @Override
    public PostResponseDTO.ReadPostListDTO readPost(PostType filter, PostStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage;

        // filter가 null일 때 전체 게시글을 조회
        if (filter == null) {
            // status가 null일 경우 전체 게시글 조회
            if (status != null) {
                postPage = postRepository.findByPostStatus(status, pageable); // 상태만으로 필터링
            } else {
                postPage = postRepository.findAll(pageable); // 전체 가져오기
            }
        } else {
            switch (filter) {
                case GROUP_PURCHASE: // 공동구매
                    if (status != null) {
                        postPage = postRepository.findByPostTypeAndPostStatus(PostType.GROUP_PURCHASE, status, pageable);
                    } else {
                        postPage = postRepository.findByPostType(PostType.GROUP_PURCHASE, pageable); // 상태가 없으면 전체
                    }
                    break;
                case SHARING: // 나눔
                    if (status != null) {
                        postPage = postRepository.findByPostTypeAndPostStatus(PostType.SHARING, status, pageable);
                    } else {
                        postPage = postRepository.findByPostType(PostType.SHARING, pageable); // 상태가 없으면 전체
                    }
                    break;
                default: // 그외
                    if (status != null) {
                        postPage = postRepository.findByPostStatus(status, pageable); // 상태만으로 필터링
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

    @Override
    @Transactional
    public Post updatePost(Long postId, Long participantCount, PostStatus postStatus){
        Optional<Post> optionalPost = postRepository.findById(postId);
;
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            if (participantCount != null) {
                post.setParticipantCount(participantCount);
            }

            if (postStatus != null) {
                if (postStatus == PostStatus.RESERVATION){
                    if (post.getPostType() == PostType.SHARING) {
                        post.setPostStatus(postStatus);
                    }
                    else{
                        throw new IllegalArgumentException("공동구매는 나눔중이 될 수 없습니다.");
                    }
                }
            }
            post.setUpdatedAt(LocalDateTime.now());

            return postRepository.save(post);
        }

        return null;
    }


}
