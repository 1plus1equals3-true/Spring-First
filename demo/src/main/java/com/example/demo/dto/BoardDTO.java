package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    private List<MultipartFile> files; // 속성 name="files" 통일 (<input type="file" name="files" multiple>)
    private Boolean deleteFile; // 파일 삭제 확인
}