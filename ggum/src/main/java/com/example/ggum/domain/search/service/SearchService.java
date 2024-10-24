package com.example.ggum.domain.search.service;

import com.example.ggum.domain.search.dto.SearchDTO;
import com.example.ggum.domain.post.entity.Post; // Post import
import com.example.ggum.domain.search.repository.SearchRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;

    public List<Post> searchPosts(SearchDTO searchDTO) {
        // 카테고리와 키워드를 기반으로 게시물 검색
        return searchRepository.findByCategoryAndKeyword(SearchDTO.getSelectedCategory(), searchDTO.getKeyword());
    }
}
