package com.example.ggum.domain.post.entity;

import com.example.ggum.domain.post.entity.status.PostCategory;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.entity.status.PostType;
import jakarta.persistence.*;
import lombok.*;
import com.example.ggum.domain.user.entity.User;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory postCategory;

    @Column()
    private Long price;

    @Column(name="participant_limit", nullable = false)
    private Long participantLimit;

    @Builder.Default
    @Column(name="participant_count", nullable = false)
    private Long participantCount=1L;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name="chatroom_count")
    private Long chatRoomCount=0L;

    //@Column(name="end_date", nullable = false)
    //private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PostStatus postStatus=PostStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name="post_type", nullable = false)
    private PostType postType;

    @Override
    public String toString() {
        return "Post{" +
                "Id=" + Id +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", postCategory=" + postCategory +
                ", price=" + price +
                ", participantLimit=" + participantLimit +
                ", participantCount=" + participantCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", chatRoomCount=" + chatRoomCount +
                ", postStatus=" + postStatus +
                ", postType=" + postType +
                '}';
    }
}
