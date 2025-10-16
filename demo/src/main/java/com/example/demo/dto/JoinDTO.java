package com.example.demo.dto;

import com.example.demo.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinDTO {
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

    private LocalDate birth;
    private String edithobby;
    private String originalfile;
    private String dir;

    public MemberEntity toEntity(String dir, String originalfile) {
        return MemberEntity.builder()
                .userid(userid)
                .pwd(pwd1)
                .name(name)
                .gender(gender)
                .birth(LocalDate.of(
                        Integer.parseInt(yyyy),
                        Integer.parseInt(mm),
                        Integer.parseInt(dd)
                ))
                .hobby((hobby != null && !hobby.isEmpty())?String.join(",", hobby):null)
                .regdate(LocalDateTime.now())
                .memberRank(1L)
                .originalfile(originalfile)
                .dir(dir)
                .build();
    }
}