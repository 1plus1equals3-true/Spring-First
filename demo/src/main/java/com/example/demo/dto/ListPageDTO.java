package com.example.demo.dto;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListPageDTO {
    // Controller -> Service
    private int page;       // 현재 페이지 번호
    private String key;
    private String word;

    // Service -> Controller
    private Page<MemberEntity> memberList;  // 페이지네이션된 회원 엔티티 목록
    private Page<BoardEntity> boardList;    // 페이지네이션된 글 엔티티 목록
    private int nowPage;                    // 현재 페이지
    private int startPage;                  // 페이지 블록 시작 번호
    private int endPage;                    // 페이지 블록 끝 번호
    private int totalPages;                 // 전체 페이지 수
    private long startNumber;               // 글번호 계산용 시작 번호
}
