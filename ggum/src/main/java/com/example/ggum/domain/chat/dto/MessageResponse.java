package com.example.ggum.domain.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@NoArgsConstructor
public class MessageResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String username;

    public MessageResponse(Long id, String content, LocalDateTime createdAt, String username) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.username = username;
    }
}
