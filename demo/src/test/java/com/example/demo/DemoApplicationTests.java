package com.example.demo;

import com.example.demo.entity.BoardEntity;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;


//    @Test
////    void test1() {
////        MemberEntity foundMember = memberRepository.findByUserid("hong");
////        List<BoardEntity> boards = foundMember.getBoard();
////        boards.forEach(board -> System.out.println("Board Title: " + board.getTitle()));
////    }

    @Test
    void test2() {
        List<BoardEntity> list = boardRepository.findAll();
        for(BoardEntity b : list) {
            System.out.println(b);
            System.out.println("-----------------------------");
        }
    }
}
