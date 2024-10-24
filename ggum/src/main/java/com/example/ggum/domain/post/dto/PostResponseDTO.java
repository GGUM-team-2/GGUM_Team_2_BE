package com.example.ggum.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResultDTO{
        Long postId;
        LocalDateTime createdAt;

        @Override
        public String toString() {
            return "PostResultDTO{" +
                    "postId=" + postId +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }


}