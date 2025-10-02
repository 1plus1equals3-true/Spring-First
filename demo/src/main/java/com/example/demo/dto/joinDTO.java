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

}
