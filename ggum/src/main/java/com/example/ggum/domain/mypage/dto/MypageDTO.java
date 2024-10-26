package com.example.ggum.domain.mypage.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MypageDTO {
    private Long postId;
    private Long userId;
    private String title;
    private String postCategory;
    private Long price;
    private Long participantLimit;
    private Long participantCount;
    private LocalDateTime updatedAt;
}
