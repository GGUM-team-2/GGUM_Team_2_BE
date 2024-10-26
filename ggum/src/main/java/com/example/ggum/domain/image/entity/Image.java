package com.example.ggum.domain.image.entity;

import com.example.ggum.domain.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter //유저들이 생성할 수 있도록 setter로 열어둠
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="image_id",unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name="image_name",unique = true)
    private String imageName;  // post_id가 같으면 하나의 게시글에 여러가지 사진이 존재하는 것

    protected Image() {}

    public Image(Post post, String imageName) {
        this.post = post;
        this.imageName = imageName;
    }
}