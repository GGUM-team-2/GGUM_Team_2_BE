package com.example.ggum.domain.mypage.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MypageDTO {
    private Long id;            // 엔티티에서 일관된 이름 사용
    private Long userId;
    private String title;
    private String postCategory;
    private Long price;
    private Long participantLimit;
    private Long participantCount;
    private LocalDateTime updatedAt;
    private Long likeuserId;    // 엔티티와 분리하여 필요 시 Service에서 처리
}
