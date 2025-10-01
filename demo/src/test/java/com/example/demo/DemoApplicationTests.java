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
	void contextLoads() {
	}

    @Test
    void saveTest() {
        MemberEntity memberEntity = new MemberEntity();

        memberEntity.setName("홍길동");
        memberEntity.setPwd("1234");
        memberEntity.setUserid("dong");
        memberEntity.setRegdate(new Date());

        memberRepository.save(memberEntity);
    }

    @Test
    void ReadTest() {
        List<MemberEntity> list = memberRepository.findAll();
        //System.out.println(list);
        System.out.println("------------");
        for (MemberEntity m : list) {
            System.out.println(m);
            System.out.println("------------");
        }
    }

    @Test
    void DeleteTest() {
        memberRepository.deleteById(1L); // long타입이라 L붙임
    }

    @Test
    void UpdateTest() {

        MemberEntity member = null;

        Optional<MemberEntity> opt = memberRepository.findById(12L); // long타입이라 L붙임
        if(opt.isPresent()) {
            member = opt.get();
        }

        System.out.println(member);
        member.setName("길동");
        member.setPwd("2345");

        memberRepository.save(member);
    }

}
