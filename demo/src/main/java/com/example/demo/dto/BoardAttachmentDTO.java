package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardAttachmentDTO {
    private long idx;               // 첨부파일 고유 ID
    private long bidx;              // 게시글 ID
    private String originalfile;    // 원본 파일명
    private String dir;             // 저장된 경로
}