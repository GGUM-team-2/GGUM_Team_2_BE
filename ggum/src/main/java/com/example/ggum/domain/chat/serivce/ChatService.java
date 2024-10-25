package com.example.ggum.domain.chat.serivce;

import com.example.ggum.domain.chat.dto.ChatMessage;
import com.example.ggum.domain.chat.dto.MessageResponse;
import com.example.ggum.domain.chat.entity.ChatRoom;
import com.example.ggum.domain.chat.entity.JoinChat;
import com.example.ggum.domain.chat.entity.Message;
import com.example.ggum.domain.chat.repository.ChatRoomRepository;
import com.example.ggum.domain.chat.repository.JoinChatRepository;
import com.example.ggum.domain.chat.repository.MessageRepository;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.repository.PostRepository;
import com.example.ggum.domain.user.entity.User;
import com.example.ggum.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JoinChatRepository joinChatRepository;
    private final PostRepository postRepository;

    //메세지 브로드캐스팅할때 사용됨
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void sendMessage(ChatMessage chatMessage) {

        User user = userRepository.findById(chatMessage.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        }

        ChatRoom room = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 채팅방에 초대된 구성원이 맞는지 검증
        boolean isMember = joinChatRepository.existsByUserAndChatRoom(user, room);
        if (!isMember) {
            throw new IllegalArgumentException("해당 채팅방의 구성원이 아닙니다.");
        }

        // 메시지 타입이 CHAT일 때만 DB에 저장
        if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            Message message = Message.builder()
                    .content(chatMessage.getContent())
                    .user(user)
                    .chatRoom(room)
                    .build();
            messageRepository.save(message);
        }

        // 메시지를 브로드캐스트 (CHAT, ENTER, LEAVE 모두 브로드캐스트)
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }



    @Transactional
    public void inviteUserToChatRoom(Long roomId, Long userId) { //새로운 유저를 채팅방에 초대할 경우
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User newUser = userRepository.findById(userId);
        if (newUser == null) {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        }


        // 채팅방에 유저를 초대
        JoinChat joinChatting = new JoinChat(newUser, room);
        joinChatRepository.save(joinChatting);

        // 입장 메시지 전송
        ChatMessage welcomeMessage = new ChatMessage();
        welcomeMessage.setContent(newUser.getUsername() + "님이 입장하셨습니다.");
        welcomeMessage.setRoomId(roomId);
        welcomeMessage.setType(ChatMessage.MessageType.ENTER);

        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, welcomeMessage);
    }

    @Transactional
    public void handleLeave(ChatMessage chatMessage) { //유저가 채팅방에서 나갔을 경우
        chatMessage.setContent(chatMessage.getUsername() + "님이 퇴장하셨습니다.");
        chatMessage.setType(ChatMessage.MessageType.LEAVE);

        // 퇴장 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);

        // 사용자를 채팅방에서 제거
        joinChatRepository.deleteByUserIdAndChatRoomId(chatMessage.getUserId(), chatMessage.getRoomId());

        //메세지 삭제
        messageRepository.deleteByUserIdAndChatRoomId(chatMessage.getUserId(), chatMessage.getRoomId());

        // 채팅방에 남은 유저 수 확인
        int remainingUsers = joinChatRepository.countByChatRoomId(chatMessage.getRoomId());

        // 남은 유저가 없다면 채팅방을 삭제
        if (remainingUsers == 0) {
            ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + chatMessage.getRoomId()));

            Post post = postRepository.findById(chatRoom.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다: " + chatRoom.getPostId()));

            post.setChatRoomCount(post.getChatRoomCount() - 1);
            postRepository.save(post);

            chatRoomRepository.deleteById(chatMessage.getRoomId());
        }
    }

    //채팅방에 모든 메세지 가져오기
    public List<MessageResponse> getChatRoomMessages(Long roomId) {
        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
        return messages.stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getContent(),
                        message.getCreatedAt(),
                        message.getUser().getUsername()
                ))
                .collect(Collectors.toList());
    }

}
