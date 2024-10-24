package com.example.ggum.domain.mypage.repository;

import com.example.ggum.domain.Mypage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MypageRepository extends JpaRepository<Mypage, Long> {
    Mypage findById(Long id);
}