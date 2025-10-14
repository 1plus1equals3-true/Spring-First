package com.example.demo.repository;

import com.example.demo.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByUserid(String userid);
    Page<MemberEntity> findByUseridContaining(String word, Pageable pageable);
    Page<MemberEntity> findByNameContaining(String word, Pageable pageable);
    Page<MemberEntity> findByHobbyContaining(String word, Pageable pageable);
}
