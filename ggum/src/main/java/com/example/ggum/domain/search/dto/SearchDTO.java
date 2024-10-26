package com.example.ggum.domain.search.dto;

import com.example.ggum.domain.post.entity.status.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    private PostCategory selectedCategory; // 선택된 카테고리
    private String keyword;           // 검색 키워드
}
