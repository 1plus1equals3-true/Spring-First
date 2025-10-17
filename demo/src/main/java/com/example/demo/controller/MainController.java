package com.example.demo.controller;

import com.example.demo.dto.JoinDTO;
import com.example.demo.dto.ListPageDTO;
import com.example.demo.dto.MemberViewDTO;
import com.example.demo.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;

    @Value("${file.upload.base-dir}")
    private String UPLOAD_BASE_DIR;

    @GetMapping(value = {"/","","/index"})
    public String home() {
        System.out.println("---------------> home");
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
        System.out.println("---------------> join");
        return "member/Join.html";
    }

    @GetMapping(value = {"/member/list"})
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(name = "key", defaultValue = "userid") String key,
                       @RequestParam(name = "word", defaultValue = "") String word) {

        System.out.println("---------------> list");

        // 서비스로 넘길 DTO 생성
        ListPageDTO requestDto = new ListPageDTO();
        requestDto.setPage(page);
        requestDto.setKey(key);
        requestDto.setWord(word);

        // 서비스에 로직 위임후 DTO를 반환
        ListPageDTO listDto = memberService.list(requestDto);

        model.addAttribute("list", listDto.getMemberList());        // 페이지 데이터
        model.addAttribute("nowPage", listDto.getNowPage());        // 현재 페이지
        model.addAttribute("startPage", listDto.getStartPage());    // 페이지 시작 번호
        model.addAttribute("endPage", listDto.getEndPage());        // 페이지 끝 번호
        model.addAttribute("totalPages", listDto.getTotalPages());  // 전체 페이지 수

        model.addAttribute("key", key);
        model.addAttribute("word", word);

        return "member/list.html";
    }

    @PostMapping(value = {"/member/delete"})
    public String delete(@RequestParam("idx") long idx,
                         RedirectAttributes redirectAttributes) {
        System.out.println("---------------> delete " + idx);

        try {
            memberService.delete(idx);

            // 성공 시
            return "redirect:/member/list";

        } catch (NoSuchElementException e) {
            // 회원을 찾을 수 없을 때
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            redirectAttributes.addFlashAttribute("url", "/member/list");
            return "redirect:/error_page";

        } catch (Exception e) {
            // 기타 DB 또는 파일 처리 중 발생하는 모든 예외
            System.err.println("회원 삭제 중 예외 발생: " + e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "회원 정보 삭제 처리 중 오류가 발생했습니다.");
            redirectAttributes.addFlashAttribute("url", "/member/list");
            return "redirect:/error_page";
        }
    }

    @GetMapping(value = "/member/view")
    public String view(Model model,
                       @RequestParam("idx") long idx) {
        System.out.println("---------------> view");

        MemberViewDTO memberDto = memberService.view(idx);
        model.addAttribute("member", memberDto);

        return "member/view.html";
    }

    @PostMapping(value = "/member/edit")
    public String edit(Model model,
                       @RequestParam("idx") long idx,
                       RedirectAttributes redirectAttributes) {
        System.out.println("---------------> edit");

        try {
            JoinDTO memberDto = memberService.edit(idx);
            model.addAttribute("member", memberDto);
            return "member/edit.html";

        } catch (NoSuchElementException e) {
            // 회원을 찾을 수 없을 때
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            redirectAttributes.addFlashAttribute("url", "/member/list");
            return "redirect:/error_page";
        }
    }


    @PostMapping(value = "/member/edit_proc")
    public String edit_proc(JoinDTO dto,
                            RedirectAttributes redirectAttributes) {
        System.out.println("---------------> edit_proc");

        try {
            memberService.editProc(dto);

            // 성공 시
            return "redirect:/member/view?idx=" + dto.getIdx();

        } catch (NoSuchElementException e) {
            // 회원을 찾을 수 없을 때
            redirectAttributes.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
            redirectAttributes.addFlashAttribute("url", "/member/list");
            return "redirect:/error_page";

        } catch (RuntimeException e) {
            // 파일 오류나 생년월일 파싱 오류 처리
            System.err.println("수정 처리 중 Runtime 예외 발생: " + e.getMessage());
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            // 수정 폼으로 다시 돌아가도록 설정
            redirectAttributes.addFlashAttribute("url", "/member/edit?idx=" + dto.getIdx());
            return "redirect:/error_page";

        } catch (Exception e) {
            // 기타 모든 예외 처리
            System.err.println("기타 수정 처리 중 예외 발생: " + e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "회원 정보 수정 중 알 수 없는 오류가 발생했습니다.");
            redirectAttributes.addFlashAttribute("url", "/member/list");
            return "redirect:/error_page";
        }
    }

    @PostMapping(value = "/member/Join_proc")
    public String Join_proc(JoinDTO dto,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        System.out.println("---------------> join_proc");

        boolean insert = memberService.insert(dto);
        model.addAttribute("dto", dto);

        if(insert){
            return "redirect:/member/list";
        }else {
            redirectAttributes.addFlashAttribute("msg", "저장오류");
            redirectAttributes.addFlashAttribute("url", "/member/Join");
            return "redirect:/error_page";
        }
    }
}