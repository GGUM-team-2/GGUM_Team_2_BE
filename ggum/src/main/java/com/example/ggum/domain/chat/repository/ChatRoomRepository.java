package com.example.ggum.domain.chat.repository;

import com.example.ggum.domain.chat.entity.ChatRoom;
import com.example.ggum.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByPostId(Long postId);
}
