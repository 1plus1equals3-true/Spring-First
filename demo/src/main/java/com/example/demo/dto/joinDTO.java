package com.example.demo.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class joinDTO {
    private long idx;
    private String userid;
    private String pwd1;
    private String pwd2;
    private String name;
    private String gender;
    private String yyyy;
    private String mm;
    private String dd;
    private List<String> hobby;
    private long member_rank;
    private MultipartFile upfile;
    private Boolean deleteFile; // 파일 삭제 확인
    //private String idCheckResult; // "y" 또는 "n" 값 dto이용한 json으로 받을때 사용
}