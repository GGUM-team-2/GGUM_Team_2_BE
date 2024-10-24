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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadPostDTO { // ReadPostDTO를 static으로 추가
        private Long postId;
        private String title;
        private String content;
        private String category;
        private double price;
        private LocalDateTime createdAt;
        private Long participantCount;
        private Long participantLimit;

        @Override
        public String toString() {
            return "ReadPostDTO{" +
                    "postId=" + postId +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", category='" + category + '\'' +
                    ", price=" + price +
                    ", createdAt=" + createdAt +
                    ", participantCount=" + participantCount +
                    ", participantLimit=" + participantLimit +
                    '}';
        }
    }


}