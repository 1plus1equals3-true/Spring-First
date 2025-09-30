package com.example.demo.controller;

import com.example.demo.dto.joinDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {

    @GetMapping(value = {"/","","/index"})
    public String home() {
        System.out.println("home");
        return "home.html";
    }

    @GetMapping(value = "/member/Join")
    public String Join() {
        System.out.println("join");
        return "member/Join.html";
    }

    @PostMapping(value = "/member/Join_proc")
    public String Join_proc(joinDTO dto, @RequestParam(value="uid", defaultValue="default") String uid, Model model) {

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

        model.addAttribute("uid", uid);
        model.addAttribute("dto", dto);

        return "member/result.html";
    }
}