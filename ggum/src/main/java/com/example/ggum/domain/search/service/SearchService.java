package com.example.ggum.domain.search.service;

import com.example.ggum.domain.search.dto.SearchDTO;
import com.example.ggum.domain.search.dto.SearchResponseDTO;
import com.example.ggum.domain.search.repository.SearchRepository;
import com.example.ggum.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;

    public List<SearchResponseDTO> searchPosts(SearchDTO searchDTO) {
        // 선택된 카테고리와 키워드를 기반으로 게시물 검색
        List<Post> posts = searchRepository.findByCategoryAndKeyword(
                searchDTO.getSelectedCategory(), searchDTO.getKeyword());

        // 검색 결과를 SearchResponseDTO로 변환하여 반환
        return posts.stream()
                .map(post -> new SearchResponseDTO(post.getId(), post.getTitle(), post.getContent()))
                .collect(Collectors.toList());
    }
}
