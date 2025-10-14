package com.example.demo;

import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    MemberRepository memberRepository;


    @Test
    void test() {
        List<MemberEntity> memberEntity =  memberRepository.findByUseridContaining("hong");
        System.out.println(memberEntity);
    }
}
