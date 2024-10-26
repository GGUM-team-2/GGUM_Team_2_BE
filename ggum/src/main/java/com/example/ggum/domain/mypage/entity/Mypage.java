package com.example.ggum.domain.mypage.entity;

import com.example.ggum.domain.post.entity.status.PostCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mypage")
@Getter
@Setter
@NoArgsConstructor
public class Mypage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 엔티티의 고유 식별자

    @Column(name = "user_id")
    private Long userId;

    private String title;

    @Column(name = "post_category")
    private PostCategory postCategory;

    private Long price;

    @Column(name = "participant_limit")
    private Long participantLimit;

    @Column(name = "participant_count")
    private Long participantCount;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
