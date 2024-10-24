package com.example.ggum.domain.post.converter;

import com.example.ggum.domain.post.entity.status.PostCategory;
import com.example.ggum.domain.post.entity.status.PostStatus;
import com.example.ggum.domain.post.entity.status.PostType;

public class PostMapper {

    public static PostCategory toPostCategory(String input) {
        try {
            return PostCategory.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category value: " + input);
        }
    }

    public static PostStatus toPostStatus(String input) {
        try {
            return PostStatus.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + input);
        }
    }

    public static PostType toPostType(String input) {
        try {
            return PostType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid post type value: " + input);
        }
    }
}
