package com.example.ggum.domain.mypage.dto;

import com.example.ggum.domain.post.entity.status.PostCategory;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MypageDTO {
    private Long id;            // 엔티티에서 일관된 이름 사용
    private Long userId;
    private String title;
    private PostCategory postCategory;
    private Long price;
    private Long participantLimit;
    private Long participantCount;
    private LocalDateTime updatedAt;



}
