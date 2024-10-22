package com.example.ggum.domain.chat.entity;

import com.example.ggum.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="massage_id",unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
