package com.example.ggum.domain.mypage.entity;

import com.example.ggum.domain.post.entity.Post;
import java.time.LocalDateTime;

public class Mypage {

    private Long postId;
    private Long userId;
    private String title;
    private String postCategory;
    private Long price;
    private Long participantLimit;
    private Long participantCount;
    private LocalDateTime updatedAt;

    // Post 객체로부터 데이터를 받아 Mypage 객체 생성
    public Mypage(Post post) {
        this.postId = post.getId();
        this.userId = post.getUser().getId();
        this.title = post.getTitle();
        this.postCategory = post.getPostCategory().name();
        this.price = post.getPrice();
        this.participantLimit = post.getParticipantLimit();
        this.participantCount = post.getParticipantCount();
        this.updatedAt = post.getUpdatedAt();
    }
}
