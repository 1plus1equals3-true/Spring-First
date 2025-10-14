package com.example.demo.controller;

import com.example.demo.dto.boardDTO;
import com.example.demo.dto.joinDTO;
import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
public class BoardController {

    @Autowired
    BoardRepository boardRepository;

    @Value("${file.upload.base-dir}")
    private String UPLOAD_BASE_DIR;

    @GetMapping(value = {"/board/write"})
    public String home() {
        System.out.println("---------------> write");
        return "board/write.html";
    }

    @PostMapping(value = "/board/write_proc")
    public String Join_proc(boardDTO dto,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {

        System.out.println("---------------> write_proc");

        model.addAttribute("dto", dto);

        BoardEntity boardEntity = new BoardEntity();

//        MultipartFile upfile = dto.getUpfile();
//
//        String originalfile = null;
//        String dir = null;
//
//        if (upfile != null && !upfile.isEmpty()) {
//            try {
//                LocalDate today = LocalDate.now();
//                String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//
//                String relativeDirName = "Spring" + dateString;
//                String fullUploadPath = UPLOAD_BASE_DIR + relativeDirName;
//
//                File directory = new File(fullUploadPath);
//                if (!directory.exists()) {
//                    directory.mkdirs();
//                }
//
//                // 이름 중복 처리
//                String originalFilenameFromForm = upfile.getOriginalFilename();
//                String extension = "";
//                int dotIndex = originalFilenameFromForm.lastIndexOf(".");
//                if (dotIndex > 0) {
//                    extension = originalFilenameFromForm.substring(dotIndex);
//                }
//
//                String uuid = UUID.randomUUID().toString();
//                String storedFilename = uuid + extension; // UUID로 변경된 파일 이름
//
//                // 파일 저장
//                File dest = new File(fullUploadPath, storedFilename);
//                upfile.transferTo(dest);
//
//                originalfile = originalFilenameFromForm;
//                dir = relativeDirName + "/" + storedFilename;
//
//            } catch (IOException e) {
//                System.err.println("파일 저장 중 I/O 오류 발생: " + e.getMessage());
//                redirectAttributes.addFlashAttribute("msg", "파일 저장 오류");
//                redirectAttributes.addFlashAttribute("url", "/member/Join");
//                return "redirect:/error_page";
//            } catch (Exception e) {
//                System.err.println("기타 파일 업로드 오류: " + e.getMessage());
//            }
//        }
//
//        if (dir != null) {
//            memberEntity.setDir(dir);
//            memberEntity.setOriginalfile(originalfile);
//        }

        boardEntity.setName(dto.getName());
        boardEntity.setPwd(dto.getPwd());
        boardEntity.setTitle(dto.getTitle());
        boardEntity.setContent(dto.getContent());
        boardEntity.setRegdate(LocalDateTime.now());
        String clientIp = request.getRemoteAddr();
        boardEntity.setIp(clientIp);

        try{
            boardRepository.save(boardEntity);
            return "redirect:/";
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "저장오류");
            redirectAttributes.addFlashAttribute("url", "/board/write");
            return "redirect:/error_page";
        }
    }
}