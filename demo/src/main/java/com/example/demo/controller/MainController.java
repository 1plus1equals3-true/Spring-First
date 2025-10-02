package com.example.demo.controller;

import com.example.demo.dto.joinDTO;
import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    MemberRepository memberRepository;

    @GetMapping(value = {"/","","/index"})
    public String home() {
        System.out.println("home");
        return "home.html";
    }

    @GetMapping(value = {"/error_page"})
    public String error_page(Model model) {

        String msg = model.getAttribute("msg").toString();
        String url = model.getAttribute("url").toString();

        model.addAttribute("msg",msg);
        model.addAttribute("url",url);
        return "error_page.html";
    }

    @GetMapping(value = "/member/Join")
    public String Join() {
        System.out.println("join");
        return "member/Join.html";
    }

    @GetMapping(value = {"/member/list"})
    public String list(Model model) {
        System.out.println("list");
        List<MemberEntity> list = memberRepository.findAll();
        model.addAttribute("list", list);
        return "member/list.html";
    }

    @PostMapping(value = {"/member/delete"})
    public String delete(@RequestParam("idx") long idx) {
        System.out.println("delete" + idx);
        memberRepository.deleteById(idx);
        return "redirect:/member/list";
    }

    @GetMapping(value = "/member/view")
    public String view(Model model,
                       @RequestParam("idx") long idx) {
        System.out.println("view");

        MemberEntity member = memberRepository.findById(idx).orElseThrow(null);
        model.addAttribute("member", member);

        return "member/view.html";
    }

    @PostMapping(value = "/member/edit")
    public String edit(Model model,
                       @RequestParam("idx") long idx) {
        System.out.println("edit");

        MemberEntity member = memberRepository.findById(idx).orElseThrow(null);
        model.addAttribute("member", member);

        return "member/edit.html";
    }

    // DB수정은 되는데 뷰페이지로 안감 수정해야함
    @PostMapping(value = "/member/edit_proc")
    public String edit_proc(Model model,
                            joinDTO dto,
                            RedirectAttributes redirectAttributes) {
        System.out.println("edit_proc");

        model.addAttribute("dto", dto);
        model.addAttribute("idx", dto.getIdx());

        MemberEntity memberEntity = memberRepository.findById(dto.getIdx()).orElseThrow(null);

        memberEntity.setPwd1(dto.getPwd1());
        memberEntity.setPwd2(dto.getPwd2());
        memberEntity.setName(dto.getName());
        memberEntity.setGender(dto.getGender());
        try {
            // DTO의 yyyy, mm, dd를 Integer로 변환하여 LocalDate 객체 생성
            LocalDate birthDate = LocalDate.of(
                    Integer.parseInt(dto.getYyyy()),
                    Integer.parseInt(dto.getMm()),
                    Integer.parseInt(dto.getDd())
            );

            // 엔티티에 LocalDate 객체 설정
            memberEntity.setBirth(birthDate);

        } catch (Exception e) {
            System.err.println("생년월일 변환 오류: " + e.getMessage());
            memberEntity.setBirth(null);
        }

        List<String> hobbies = dto.getHobby();
        if (hobbies != null && !hobbies.isEmpty()) {
            memberEntity.setHobby(String.join(",", hobbies));
        } else {
            memberEntity.setHobby(null); // 또는 memberEntity.setHobby("");
        }

        try{
            memberRepository.save(memberEntity);
            return "redirect:/member/view";
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "수정오류");
            redirectAttributes.addFlashAttribute("url", "/member/list");
            return "redirect:/error_page";
        }
    }

    @PostMapping(value = "/member/Join_proc")
    public String Join_proc(joinDTO dto,
                            @RequestParam(value="uid", defaultValue="default") String uid,
                            Model model,
                            RedirectAttributes redirectAttributes) {
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

        try {
            // DTO의 yyyy, mm, dd를 Integer로 변환하여 LocalDate 객체 생성
            LocalDate birthDate = LocalDate.of(
                    Integer.parseInt(dto.getYyyy()),
                    Integer.parseInt(dto.getMm()),
                    Integer.parseInt(dto.getDd())
            );

            // 엔티티에 LocalDate 객체 설정
            memberEntity.setBirth(birthDate);

        } catch (Exception e) {
            System.err.println("생년월일 변환 오류: " + e.getMessage());
            memberEntity.setBirth(null);
        }

        List<String> hobbies = dto.getHobby();
        if (hobbies != null && !hobbies.isEmpty()) {
            memberEntity.setHobby(String.join(",", hobbies));
        } else {
            memberEntity.setHobby(null); // 또는 memberEntity.setHobby("");
        }
        memberEntity.setRegdate(LocalDateTime.now());
        memberEntity.setMember_rank(1L);

        try{
            memberRepository.save(memberEntity);
            return "redirect:/member/list";
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "저장오류");
            redirectAttributes.addFlashAttribute("url", "/member/Join");
            return "redirect:/error_page";
        }
    }
}