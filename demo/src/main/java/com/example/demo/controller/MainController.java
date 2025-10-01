package com.example.demo.controller;

import com.example.demo.dto.joinDTO;
import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    MemberRepository memberRepository;

    @GetMapping(value = {"/","","/index"})
    public String home() {
        System.out.println("home");
        /*
        MemberEntity memberEntity = new MemberEntity();

        memberEntity.setName("홍길동");
        memberEntity.setPwd("1234");
        memberEntity.setUserid("dong");
        memberEntity.setRegdate(new Date());

        memberRepository.save(memberEntity);
         */
        return "home.html";
    }

    @GetMapping(value = "/member/Join")
    public String Join() {
        System.out.println("join");
        return "member/Join.html";
    }

    @PostMapping(value = "/member/Join_proc")
    public String Join_proc(joinDTO dto, @RequestParam(value="uid", defaultValue="default") String uid, Model model) {
        /*
        MultipartFile upfile = dto.getUpfile();

        if (upfile != null && !upfile.isEmpty()) {
            System.out.println("업로드된 파일 이름: " + upfile.getOriginalFilename());
            System.out.println("업로드된 파일 크기: " + upfile.getSize() + " bytes");

            try {
                model.addAttribute("fileName", upfile.getOriginalFilename());
                model.addAttribute("fileSize", upfile.getSize());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("업로드된 파일이 없습니다.");
        }
         */

        model.addAttribute("dto", dto);

        MemberEntity memberEntity = new MemberEntity();

        memberEntity.setUserid(dto.getUserid());
        memberEntity.setPwd1(dto.getPwd1());
        memberEntity.setPwd2(dto.getPwd2());
        memberEntity.setName(dto.getName());
        memberEntity.setGender(dto.getGender());
        memberEntity.setBirth(new Date());
        List<String> hobbies = dto.getHobby();
        if (hobbies != null && !hobbies.isEmpty()) {
            // 취미가 선택된 경우에만 ,로 연결하여 문자열로 저장
            memberEntity.setHobby(String.join(",", hobbies));
        } else {
            // 취미가 선택되지 않은 경우, DB의 NULL 허용에 맞게 NULL 또는 빈 문자열("") 저장
            // DB에서 NULL을 허용하므로, 명시적으로 NULL을 설정하거나 빈 문자열을 사용합니다.
            memberEntity.setHobby(null); // 또는 memberEntity.setHobby("");
        }
        memberEntity.setRegdate(new Date());
        memberEntity.setMember_rank(1L);

        memberRepository.save(memberEntity);

        return "member/result.html";
    }
}