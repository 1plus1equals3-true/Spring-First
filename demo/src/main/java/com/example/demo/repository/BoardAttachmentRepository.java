package com.example.demo.repository;

import com.example.demo.entity.BoardAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardAttachmentRepository extends JpaRepository<BoardAttachmentEntity, Long> {
    List<BoardAttachmentEntity> findByBidx(long bidx);
}
