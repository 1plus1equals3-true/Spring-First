package com.example.demo.dto;

import com.example.demo.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberViewDTO {
    // 뷰 화면에 표시할 필드만 정의
    private long idx;
    private String userid;
    private String pwd;
    private String name;
    private String gender;
    private LocalDate birth;
    private String hobby;
    private long memberRank;
    private String dir;
    private String originalfile;
    private LocalDateTime regDate;

    public static MemberViewDTO toEntity(MemberEntity entity) {
        return MemberViewDTO.builder()
                .idx(entity.getIdx())
                .userid(entity.getUserid())
                .pwd(entity.getPwd())
                .name(entity.getName())
                .gender(entity.getGender())
                .birth(entity.getBirth())
                .hobby(entity.getHobby())
                .memberRank(entity.getMemberRank())
                .dir(entity.getDir())
                .originalfile(entity.getOriginalfile())
                .regDate(entity.getRegdate())
                .build();
    }
}