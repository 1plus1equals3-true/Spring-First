package com.example.demo.controller;

import com.example.demo.dto.BoardDTO;
import com.example.demo.services.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

            return "redirect:/"; // 나중에 "redirect:/board/list"

        }catch (Exception e) {
            System.err.println("게시글 저장 오류: " + e.getMessage());
            redirectAttributes.addFlashAttribute("msg", "게시글 저장 중 오류가 발생했습니다.");
            redirectAttributes.addFlashAttribute("url", "/board/write");
            return "redirect:/error_page";
        }
    }
}