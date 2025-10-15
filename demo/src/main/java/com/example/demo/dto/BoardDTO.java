package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private long idx;
    private String userid;
    private String name;
    private String pwd;
    private String title;
    private String content;
    private long hit;
    private String ip;
    private long boardtype;
    private MultipartFile upfile;
    private Boolean deleteFile; // 파일 삭제 확인
}