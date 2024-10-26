package com.example.ggum.domain.search.repository;

import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.entity.status.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.postCategory = :category AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> findByCategoryAndKeyword(@Param("category") PostCategory category, @Param("keyword") String keyword);
}
