package com.example.ggum.domain.search.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //카테코리, 키워드 모두 없으면 기본 게시판 출력되도록 설정해야 되지 않을런지...?
    @Column(nullable = true)
    private String selectedCategory;

    @Column(nullable = true)
    private String keyword;
}

