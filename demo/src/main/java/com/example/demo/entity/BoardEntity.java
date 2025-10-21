package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "board")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    //private String userid;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private MemberEntity member;

    private String name;

    private String pwd;

    private String title;

    private String content;

    private long hit;

    private LocalDateTime regdate;

    private String ip;

    private long boardtype;
}
