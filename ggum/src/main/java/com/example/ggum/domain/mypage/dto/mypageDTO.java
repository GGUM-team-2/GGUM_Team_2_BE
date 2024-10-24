package com.example.ggum.domain.mypage.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/api/v1/mypage")
public class MypageController {

    @Autowired
    private MypageService mypageService;

    @GetMapping("/api/v1/id")
    public String getUserDetails(@PathVariable Long id, Model model) {
        MypageDTO userDetails = mypageService.getUserDetails(id);
        model.addAttribute("user", userDetails);
        return "mypage"; // mypage.html 템플릿으로 이동
    }
}