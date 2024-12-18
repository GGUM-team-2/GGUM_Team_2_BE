package com.example.ggum.domain.chat.entity;

import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter //유저들이 생성할 수 있도록 setter로 열어둠
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_room_id",unique = true)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator; // 채팅방을 생성한 유저

    @Column(name = "post_id", nullable = false)
    private Long postId;

}