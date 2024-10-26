package com.example.ggum.domain.search.controller;

import com.example.ggum.domain.search.dto.SearchDTO;
import com.example.ggum.domain.search.dto.SearchResponseDTO;
import com.example.ggum.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @PostMapping
    public List<SearchResponseDTO> search(@RequestBody SearchDTO searchDTO) {
        // 검색 중 상태 반환
        System.out.println("검색 중...");

        // 검색 수행 및 결과 반환
        List<SearchResponseDTO> results = searchService.searchPosts(searchDTO);

        return results;
    }
}
