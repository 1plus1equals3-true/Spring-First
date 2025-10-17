package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    Page<BoardEntity> findByNameContaining(String word, Pageable pageable);
    Page<BoardEntity> findByTitleContaining(String word, Pageable pageable);
    Page<BoardEntity> findByContentContaining(String word, Pageable pageable);
}
