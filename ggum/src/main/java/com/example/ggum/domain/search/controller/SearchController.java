package com.example.ggum.domain.search.controller;

import com.example.ggum.domain.search.dto.SearchDTO;
import com.example.ggum.domain.post.entity.Post; // Post import
import com.example.ggum.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @PostMapping
    public List<Post> search(@RequestBody SearchDTO searchDTO) {
        return searchService.searchPosts(SearchDTO);
    }
}

