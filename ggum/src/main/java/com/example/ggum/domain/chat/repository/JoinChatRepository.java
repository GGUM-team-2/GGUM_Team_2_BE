package com.example.ggum.domain.chat.repository;

import com.example.ggum.domain.chat.entity.ChatRoom;
import com.example.ggum.domain.chat.entity.JoinChat;
import com.example.ggum.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoinChatRepository  extends JpaRepository<JoinChat, Long> {
    boolean existsByUserAndChatRoom(User user, ChatRoom chatRoom);
    Long countByChatRoom(ChatRoom chatRoom);
    List<JoinChat> findAllByUserId(Long userId);

    void deleteByUserIdAndChatRoomId(Long userId, Long chatRoomId);

    // 특정 채팅방에 남은 유저 수를 세는 메서드
    int countByChatRoomId(Long chatRoomId);
    void deleteByChatRoomId(Long chatRoomId);

}
