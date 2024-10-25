package com.example.ggum.domain.chat.repository;

import com.example.ggum.domain.chat.entity.ChatRoom;
import com.example.ggum.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findFirstByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
    List<Message> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
}
