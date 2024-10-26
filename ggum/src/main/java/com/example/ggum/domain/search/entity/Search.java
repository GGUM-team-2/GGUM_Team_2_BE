/*package com.example.ggum.domain.search.entity;

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

    //만약에 키워드, 카테고리 없이 검색하면 전체가 출력되게 해야할지 고민
    @Column(nullable = true)
    private String selectedCategory;

    @Column(nullable = true)
    private String keyword;
}*/
