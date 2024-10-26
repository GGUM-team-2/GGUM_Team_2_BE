package com.example.ggum.domain.search.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResponseDTO {
    private Long postId;    // 게시물 ID
    private String title;   // 제목
    private String content; // 내용

    public SearchResponseDTO(Long postId, String title, String content) {
        this.postId = postId;
        this.title = title;
        this.content = content;
    }
}