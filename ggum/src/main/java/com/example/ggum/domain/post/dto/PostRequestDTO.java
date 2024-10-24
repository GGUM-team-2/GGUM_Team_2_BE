package com.example.ggum.domain.post.dto;

import jdk.jfr.Category;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

public class PostRequestDTO {

    @Getter
    public static class PostCreateDto{
        String title;
        String content;
        Long price;
        String category;
        String postType;
        Long participant_limit;

    }

    @Getter
    public static class PostDeleteDto{
        Long postId;
    }
}