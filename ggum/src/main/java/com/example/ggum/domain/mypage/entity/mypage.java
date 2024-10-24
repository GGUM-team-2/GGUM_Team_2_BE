package com.example.ggum.domain.mypage.entity;
import javax.persistence.*;

@Entity
@Table(name = "user")
public class mypage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //name은 user_id로 연결, profileImage는 기본으로 생성

    private String name;
    //기본 유저 사진 생성..??
    // private String profileImage;

    // Getters and Setters
}