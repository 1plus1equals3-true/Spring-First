package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class MemberEntity { //물리DB와 칼럼이 동일해야함

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    private String userid;

    private String pwd1;

    private String pwd2;

    private String name;

    private String gender;

    private Date birth;

    private String hobby;

    private Date regdate;

    private long member_rank;
}
