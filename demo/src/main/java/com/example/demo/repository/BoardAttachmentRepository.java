package com.example.demo.repository;

import com.example.demo.entity.BoardAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardAttachmentRepository extends JpaRepository<BoardAttachmentEntity, Long> {
}
