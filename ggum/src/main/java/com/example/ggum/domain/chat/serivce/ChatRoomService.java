package com.example.ggum.domain.chat.serivce;

import com.example.ggum.domain.chat.dto.ChatRoomResponse;
import com.example.ggum.domain.chat.entity.ChatRoom;
import com.example.ggum.domain.chat.entity.JoinChat;
import com.example.ggum.domain.chat.entity.Message;
import com.example.ggum.domain.chat.repository.ChatRoomRepository;
import com.example.ggum.domain.chat.repository.JoinChatRepository;
import com.example.ggum.domain.chat.repository.MessageRepository;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.status.PostType;
import com.example.ggum.domain.post.repository.PostRepository;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final JoinChatRepository joinChatRepository;
    private final PostRepository postRepository;
    private final MessageRepository messageRepository;


    // 0. 채팅방 생성
    @Transactional
    public ChatRoom createChatRoom(String roomName, Long postId, Long creatorId, List<Long> userIds) {

        User creator = userRepository.findById(creatorId);

        if (creator == null) {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다 : " + creatorId);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다: " + postId));

        // 게시글의 PostType이 GROUP_PURCHASE인 경우 채팅방이 이미 존재하는지 확인
        if (post.getPostType() == PostType.GROUP_PURCHASE) {
            Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByPostId(postId);
            if (existingChatRoom.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 채팅방입니다.");
            }
        }


        // 새로운 채팅방 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomName(roomName);
        chatRoom.setPostId(postId);
        chatRoom.setCreator(creator);
        chatRoom = chatRoomRepository.save(chatRoom);

        // 채팅방 생성자를 JoinChatting에 추가
        JoinChat creatorJoinChat = new JoinChat(creator, chatRoom);
        joinChatRepository.save(creatorJoinChat);

        // 채팅방에 유저들 초대
        for (Long userId : userIds) {
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("유저를 찾을 수 없습니다 : " + userId);
            }
            JoinChat joinChat = new JoinChat(user, chatRoom);
            joinChatRepository.save(joinChat);
        }

        post.setChatRoomCount(post.getChatRoomCount() + 1);
        postRepository.save(post);

        return chatRoom;
    }



    // 원래 존재하던 채팅방에 새로운 유저 추가
    @Transactional
    public void addUserToChatRoom(Long roomId, List<Long> userIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다 : " + roomId));

        for (Long userId : userIds) {

            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("유저를 찾을 수 없습니다 : " + userId);
            }


            // 이미 채팅방에 존재하는지 확인
            boolean isUserAlreadyInRoom = joinChatRepository.existsByUserAndChatRoom(user, chatRoom);
            if (isUserAlreadyInRoom) {
                throw new IllegalArgumentException("유저가 이미 채팅방에 있습니다: " + user.getUsername());
            }


            JoinChat joinChatting = new JoinChat(user, chatRoom);
            joinChatRepository.save(joinChatting);
        }
    }


    // 유저가 속한 채팅방 리스트 조회
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getUserChatRooms(Long userId, String postTypeFilter) {
        // 유저가 속한 모든 채팅방 가져오기
        List<ChatRoom> chatRooms = joinChatRepository.findAllByUserId(userId).stream()
                .map(JoinChat::getChatRoom)
                .collect(Collectors.toList());

        // 필터링 및 정렬 처리
        List<ChatRoom> filteredChatRooms = filterAndSortChatRooms(chatRooms, postTypeFilter);

        // 채팅방 정보 리스트로 변환
        return filteredChatRooms.stream()
                .map(chatRoom -> {
                    // Post 정보를 가져옴
                    Post post = postRepository.findById(chatRoom.getPostId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다: " + chatRoom.getPostId()));

                    // 최신 메시지를 가져옴
                    Message latestMessage = messageRepository.findFirstByChatRoomOrderByCreatedAtDesc(chatRoom)
                            .orElse(null);

                    return new ChatRoomResponse(
                            chatRoom.getId(),
                            chatRoom.getRoomName(),
                            post.getId(),
                            post.getTitle(),
                            post.getParticipantCount(),
                            post.getParticipantLimit(),
                            latestMessage != null ? latestMessage.getContent() : "메시지가 없습니다"
                    );
                })
                .collect(Collectors.toList());
    }

    // 채팅방 필터링 및 정렬
    private List<ChatRoom> filterAndSortChatRooms(List<ChatRoom> chatRooms, String postTypeFilter) {
        List<ChatRoom> filteredChatRooms = new ArrayList<>();

        // 채팅방마다 연결된 Post 정보를 가져와 필터링
        for (ChatRoom room : chatRooms) {
            Post post = postRepository.findById(room.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다: " + room.getPostId()));

            // 필터링 조건에 따라 추가 (GROUP_PURCHASE, SHARING, 또는 둘 다)
            if ("GROUP_PURCHASE".equalsIgnoreCase(postTypeFilter)) {
                if (post.getPostType() == PostType.GROUP_PURCHASE) {
                    filteredChatRooms.add(room);
                }
            } else if ("SHARING".equalsIgnoreCase(postTypeFilter)) {
                if (post.getPostType() == PostType.SHARING) {
                    filteredChatRooms.add(room);
                }
            } else { // postTypeFilter가 없으면 모든 타입을 포함
                filteredChatRooms.add(room);
            }
        }

        // 최신 작성된 순으로 정렬 (Post의 id를 기준으로)
        filteredChatRooms.sort((room1, room2) -> {
            Post post1 = postRepository.findById(room1.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다: " + room1.getPostId()));
            Post post2 = postRepository.findById(room2.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다: " + room2.getPostId()));
            return post2.getId().compareTo(post1.getId());
        });

        return filteredChatRooms;
    }

}
