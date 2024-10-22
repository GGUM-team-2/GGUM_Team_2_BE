package com.example.ggum.domain.chat.entity;

import com.example.ggum.domain.user.entity.User;
import jakarta.persistence.*;

public class JoinChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="join_chat_id",unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
}
