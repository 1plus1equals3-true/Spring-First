package com.example.demo.controller;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.ListPageDTO;
import com.example.demo.services.BoardService;
import jakarta.servlet.http.HttpServletRequest;
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
public class BoardController {

    private final BoardService boardService;

    @Value("${file.upload.base-dir}")
    private String UPLOAD_BASE_DIR;

    private String getClientIpAddress(HttpServletRequest request) {
        // 표준 X-Forwarded-For 헤더 확인 (프록시/로드 밸런서 환경)
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For는 쉼표로 구분된 IP 목록을 가질 수 있으며, 첫 번째가 클라이언트 IP
            int commaIndex = ip.indexOf(',');
            if (commaIndex > 0) {
                ip = ip.substring(0, commaIndex).trim();
            }
        }

        // 기타 프록시 헤더 확인 (특정 서버 환경)
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        // 마지막으로 기본 getRemoteAddr() 사용
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // v6 루프백ip v4로 표시
//        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
//            ip = "127.0.0.1";
//        }

        return ip;
    }

    @GetMapping(value = {"/board/list"})
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(name = "key", defaultValue = "name") String key,
                       @RequestParam(name = "word", defaultValue = "") String word) {

        System.out.println("---------------> board list");

        // 서비스로 넘길 DTO 생성
        ListPageDTO requestDto = new ListPageDTO();
        requestDto.setPage(page);
        requestDto.setKey(key);
        requestDto.setWord(word);

        // 서비스에 로직 위임후 DTO를 반환
        ListPageDTO listDto = boardService.list(requestDto);

        model.addAttribute("list", listDto.getBoardList());         // 페이지 데이터
        model.addAttribute("nowPage", listDto.getNowPage());        // 현재 페이지
        model.addAttribute("startPage", listDto.getStartPage());    // 페이지 시작 번호
        model.addAttribute("endPage", listDto.getEndPage());        // 페이지 끝 번호
        model.addAttribute("totalPages", listDto.getTotalPages());  // 전체 페이지 수
        model.addAttribute("startNumber", listDto.getStartNumber());// 글번호

        model.addAttribute("key", key);
        model.addAttribute("word", word);

        return "board/list.html";
    }

    @GetMapping(value = "/board/view")
    public String view(Model model,
                       @RequestParam("idx") long idx) {
        System.out.println("---------------> board view");

        BoardDTO boardDTO = boardService.view(idx);
        model.addAttribute("board", boardDTO);

        return "board/view.html";
    }

    @PostMapping(value = {"/board/delete"})
    public String delete(@RequestParam("idx") long idx,
                         RedirectAttributes redirectAttributes) {
        System.out.println("---------------> board delete " + idx);

        try {
            boardService.delete(idx);

            // 성공 시
            return "redirect:/board/list";

        } catch (NoSuchElementException e) {
            // 게시글을 찾을 수 없을 때
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            redirectAttributes.addFlashAttribute("url", "/board/list");
            return "redirect:/error_page";

        } catch (Exception e) {
            // 기타 DB 또는 파일 처리 중 발생하는 모든 예외
            System.err.println("회원 삭제 중 예외 발생: " + e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "게시글 삭제 처리 중 오류가 발생했습니다.");
            redirectAttributes.addFlashAttribute("url", "/board/list");
            return "redirect:/error_page";
        }
    }

    @PostMapping(value = "/board/modify")
    public String edit(Model model,
                       @RequestParam("idx") long idx,
                       RedirectAttributes redirectAttributes) {
        System.out.println("---------------> modify");

        try {
            BoardDTO boardDTO = boardService.modify(idx);
            model.addAttribute("board", boardDTO);
            return "board/modify.html";

        } catch (NoSuchElementException e) {
            // 게시글을 찾을 수 없을 때
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            redirectAttributes.addFlashAttribute("url", "/board/list");
            return "redirect:/error_page";
        }
    }

    @GetMapping(value = {"/board/write"})
    public String write() {
        System.out.println("---------------> write");
        return "board/write.html";
    }

    @PostMapping(value = "/board/write_proc")
    public String write_proc(BoardDTO dto,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {

        System.out.println("---------------> write_proc");

        String clientIp = getClientIpAddress(request);

        try{
            boardService.write(dto, clientIp);

            return "redirect:/board/list";

        }catch (Exception e) {
            System.err.println("게시글 저장 오류: " + e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "게시글 저장 중 오류가 발생했습니다.");
            redirectAttributes.addFlashAttribute("url", "/board/write");
            return "redirect:/error_page";
        }
    }
}