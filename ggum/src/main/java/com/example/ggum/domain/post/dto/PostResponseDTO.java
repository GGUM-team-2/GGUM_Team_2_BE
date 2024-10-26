package com.example.ggum.domain.post.dto;

import com.example.ggum.domain.post.entity.status.PostCategory;
import com.example.ggum.domain.post.entity.status.PostLikeStatus;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.entity.status.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
        private PostStatus postStatus;
        private PostType postType;
        private PostCategory postCategory;
        private PostLikeStatus postLikeStatus;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadPostListDTO {
        private List<PostResponseDTO.ReadPostDTO> posts;
        private int totalPosts;
        private int currentPage; // 현재 페이지
        private int totalPages;   // 총 페이지 수
    }

}