package com.example.demo.controller;

import com.example.demo.dto.joinDTO;
import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {

    @Autowired
    MemberRepository memberRepository;

    @Value("${file.upload.base-dir}")
    private String UPLOAD_BASE_DIR;

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
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "1") int page) {

        System.out.println("list");

        int pageIndex = page - 1;

        Pageable pageable = PageRequest.of(pageIndex, 10, Sort.Direction.DESC, "idx");
        Page<MemberEntity> list = memberRepository.findAll(pageable);

        int nowPage = page;
        int totalPages = list.getTotalPages();

        int startPage = Math.max(1, nowPage - 2);
        int endPage = Math.min(totalPages, nowPage + 2);

        model.addAttribute("list", list);         // 페이지 데이터
        model.addAttribute("nowPage", nowPage);   // 현재 페이지
        model.addAttribute("startPage", startPage); // 페이지 시작 번호
        model.addAttribute("endPage", endPage);     // 페이지 끝 번호
        model.addAttribute("totalPages", totalPages); // 전체 페이지 수

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
            return "redirect:/member/view?idx=" + memberEntity.getIdx();
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

        model.addAttribute("dto", dto);

        MultipartFile upfile = dto.getUpfile();
        MemberEntity memberEntity = new MemberEntity();

        String originalfile = null;
        String dir = null;

        if (upfile != null && !upfile.isEmpty()) {
            try {
                LocalDate today = LocalDate.now();
                String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                String relativeDirName = "Spring" + dateString;
                String fullUploadPath = UPLOAD_BASE_DIR + relativeDirName;

                File directory = new File(fullUploadPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // 이름 중복 처리
                String originalFilenameFromForm = upfile.getOriginalFilename();
                String extension = "";
                int dotIndex = originalFilenameFromForm.lastIndexOf(".");
                if (dotIndex > 0) {
                    extension = originalFilenameFromForm.substring(dotIndex);
                }

                String uuid = UUID.randomUUID().toString();
                String storedFilename = uuid + extension; // UUID로 변경된 파일 이름

                // 파일 저장
                File dest = new File(fullUploadPath, storedFilename);
                upfile.transferTo(dest);

                originalfile = originalFilenameFromForm;
                dir = relativeDirName + "/" + storedFilename;

            } catch (IOException e) {
                System.err.println("파일 저장 중 I/O 오류 발생: " + e.getMessage());
                redirectAttributes.addFlashAttribute("msg", "파일 저장 오류");
                redirectAttributes.addFlashAttribute("url", "/member/Join");
                return "redirect:/error_page";
            } catch (Exception e) {
                System.err.println("기타 파일 업로드 오류: " + e.getMessage());
            }
        }

        if (dir != null) {
            memberEntity.setDir(dir);
            memberEntity.setOriginalfile(originalfile);
        }

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