package com.example.ggum.domain.post.dto;

import com.example.ggum.domain.post.entity.status.PostCategory;
import com.example.ggum.domain.post.entity.status.PostType;
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
        PostCategory category;
        PostType postType;
        Long participant_limit;

    }

    @Getter
    public static class PostDeleteDto{
        Long postId;
        String content;
    }

    @Getter
    public static class PostUpdateDto{
        Long postId;
        String title;
        String content;
        Long price;
        PostCategory category;
        PostType postType;
        Long participant_limit;
    }

    @Getter
    public static class PostOneReadDto{
        Long postId;
    }


}