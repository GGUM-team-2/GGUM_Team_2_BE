package com.example.ggum.domain.mypage.repository;

import com.example.ggum.domain.mypage.entity.Mypage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MypageRepository extends JpaRepository<Mypage, Long> {
    // JpaRepository 기본 CRUD 기능
}
