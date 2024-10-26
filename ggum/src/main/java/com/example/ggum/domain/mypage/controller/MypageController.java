package com.example.ggum.domain.mypage.controller;

import com.example.ggum.domain.mypage.dto.MypageDTO;
import com.example.ggum.domain.mypage.service.MypageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mypage")
public class MypageController {

    private final MypageService mypageService;

    public MypageController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

    // 간단하게 사용자의 마이페이지 데이터를 가져오는 API
    @GetMapping("/{userId}")
    public MypageDTO getMypageData(@PathVariable Long userId) {
        return mypageService.getMypageData(userId);
    }
}
