package com.example.demo.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class joinDTO {
    private String uid;
    private String upass1;
    private String upass2;
    private String uname;
    private String sex;
    private List<String> hobby;
    private String yyyy;
    private String mm;
    private String dd;
    private MultipartFile upfile;

}
