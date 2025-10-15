package com.example.demo.controller;

import com.example.demo.dto.JoinDTO;
import com.example.demo.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

//    @GetMapping(value = {"/member/list"})
//    public String list(Model model,
//                       @RequestParam(value = "page", defaultValue = "1") int page,
//                       @RequestParam(name = "key", defaultValue = "userid") String key,
//                       @RequestParam(name = "word", defaultValue = "") String word) {
//
//        System.out.println("---------------> list");
//
//        int pageIndex = page - 1;
//        final int PAGE_SIZE = 10;    // 한 페이지에 보여줄 리스트 수
//        int blockLimit = 10;         // 페이징 수
//
//        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.Direction.DESC, "idx");
//        Page<MemberEntity> list = null;
//
//        if (word.trim().isEmpty()) {
//            list = memberRepository.findAll(pageable);
//        } else {
//            switch (key) {
//                case "userid":
//                    list = memberRepository.findByUseridContaining(word, pageable);
//                    break;
//                case "name":
//                    list = memberRepository.findByNameContaining(word, pageable);
//                    break;
//                case "hobby":
//                    list = memberRepository.findByHobbyContaining(word, pageable);
//                    break;
//                default: // 유효하지 않은 key인 경우 전체 검색
//                    list = memberRepository.findAll(pageable);
//                    break;
//            }
//        }
//
//        int nowPage = page;
//        int totalPages = list.getTotalPages();
//
//        int startPage = 0;
//        int endPage = 0;
//
//        if (totalPages > 0) { // 0 페이지 처리
//            startPage = (int)(Math.ceil(nowPage / (double)blockLimit) - 1) * blockLimit + 1;
//
//            // 전체 페이지 수를 넘지 않도록
//            endPage = Math.min(startPage + blockLimit - 1, totalPages);
//
//            // startPage가 1보다 작아지지 않도록
//            startPage = Math.max(1, startPage);
//        }
//
//        model.addAttribute("list", list);           // 페이지 데이터
//        model.addAttribute("nowPage", nowPage);     // 현재 페이지
//        model.addAttribute("startPage", startPage); // 페이지 시작 번호
//        model.addAttribute("endPage", endPage);     // 페이지 끝 번호
//        model.addAttribute("totalPages", totalPages); // 전체 페이지 수
//
//        model.addAttribute("key", key);
//        model.addAttribute("word", word);
//
//        return "member/list.html";
//    }
//
//    @PostMapping(value = {"/member/delete"})
//    public String delete(@RequestParam("idx") long idx,
//                         RedirectAttributes redirectAttributes) {
//        System.out.println("---------------> delete" + idx);
//
//        MemberEntity memberEntity = memberRepository.findById(idx).orElse(null);
//        if (memberEntity == null) {
//            redirectAttributes.addFlashAttribute("msg", "삭제할 회원 정보를 찾을 수 없습니다.");
//            redirectAttributes.addFlashAttribute("url", "/member/list");
//            return "redirect:/error_page";
//        }
//
//        String fileDir = memberEntity.getDir();
//        if (fileDir != null && !fileDir.isEmpty()) {
//            try {
//                // 'UPLOAD_BASE_DIR'과 'dir'을 합쳐 절대 경로 File 객체 생성
//                File attachedFile = new File(UPLOAD_BASE_DIR, fileDir);
//
//                if (attachedFile.exists()) {
//                    if (attachedFile.delete()) {
//                        System.out.println("첨부파일 삭제 성공: " + fileDir);
//                    } else {
//                        System.err.println("첨부파일 삭제 실패: " + fileDir);
//                    }
//                } else {
//                    System.out.println("첨부파일이 경로에 존재하지 않음: " + fileDir);
//                }
//            } catch (Exception e) {
//                System.err.println("파일 삭제 중 오류 발생: " + e.getMessage());
//            }
//        }
//
//        try {
//            memberRepository.deleteById(idx);
//            return "redirect:/member/list";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("msg", "회원 정보 삭제 오류");
//            redirectAttributes.addFlashAttribute("url", "/member/list");
//            return "redirect:/error_page";
//        }
//    }
//
//    @GetMapping(value = "/member/view")
//    public String view(Model model,
//                       @RequestParam("idx") long idx) {
//        System.out.println("---------------> view");
//
//        MemberEntity member = memberRepository.findById(idx).orElseThrow(null);
//        model.addAttribute("member", member);
//
//        return "member/view.html";
//    }
//
//    @PostMapping(value = "/member/edit")
//    public String edit(Model model,
//                       @RequestParam("idx") long idx) {
//        System.out.println("---------------> edit");
//
//        MemberEntity member = memberRepository.findById(idx).orElseThrow(null);
//        model.addAttribute("member", member);
//
//        return "member/edit.html";
//    }
//
//
//    @PostMapping(value = "/member/edit_proc")
//    public String edit_proc(Model model,
//                            joinDTO dto,
//                            RedirectAttributes redirectAttributes) {
//        System.out.println("---------------> edit_proc");
//
//        model.addAttribute("dto", dto);
//        model.addAttribute("idx", dto.getIdx());
//
//        MemberEntity memberEntity = memberRepository.findById(dto.getIdx()).orElse(null);
//        if (memberEntity == null) {
//            redirectAttributes.addFlashAttribute("msg", "회원 정보를 찾을 수 없습니다.");
//            redirectAttributes.addFlashAttribute("url", "/member/list");
//            return "redirect:/error_page";
//        }
//
//        MultipartFile upfile = dto.getUpfile();
//        String originalfile = memberEntity.getOriginalfile(); // 기존 파일명
//        String dir = memberEntity.getDir();                   // 기존 파일 경로
//
//        try {
//            // deleteFile 체크인 경우
//            if (Boolean.TRUE.equals(dto.getDeleteFile())) {
//                if (dir != null && !dir.isEmpty()) {
//                    // 삭제
//                    File oldFile = new File(UPLOAD_BASE_DIR, dir);
//                    if (oldFile.exists()) {
//                        oldFile.delete();
//                    }
//                }
//                // DB 칼럼정보 삭제
//                originalfile = null;
//                dir = null;
//            }
//
//            // 업로드 처리
//            if (upfile != null && !upfile.isEmpty()) {
//                // 기존 파일 삭제
//                if (dir != null && !dir.isEmpty()) {
//                    File oldFile = new File(UPLOAD_BASE_DIR, dir);
//                    if (oldFile.exists()) {
//                        oldFile.delete();
//                    }
//                }
//
//                // 새 파일 저장
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
//            }
//            // 새 파일도 없고 삭제 요청도 없는 경우 기존 값 유지
//
//        } catch (IOException e) {
//            System.err.println("파일 처리 중 I/O 오류 발생: " + e.getMessage());
//            redirectAttributes.addFlashAttribute("msg", "파일 저장 또는 삭제 오류");
//            redirectAttributes.addFlashAttribute("url", "/member/edit?idx=" + dto.getIdx());
//            return "redirect:/error_page";
//        } catch (Exception e) {
//            System.err.println("기타 파일 업로드 오류: " + e.getMessage());
//            return "redirect:/error_page";
//        }
//
//        memberEntity.setOriginalfile(originalfile);
//        memberEntity.setDir(dir);
//
//        memberEntity.setPwd1(dto.getPwd1());
//        memberEntity.setPwd2(dto.getPwd2());
//        memberEntity.setName(dto.getName());
//        memberEntity.setGender(dto.getGender());
//        try {
//            // DTO의 yyyy, mm, dd를 Integer로 변환하여 LocalDate 객체 생성
//            LocalDate birthDate = LocalDate.of(
//                    Integer.parseInt(dto.getYyyy()),
//                    Integer.parseInt(dto.getMm()),
//                    Integer.parseInt(dto.getDd())
//            );
//
//            // 엔티티에 LocalDate 객체 설정
//            memberEntity.setBirth(birthDate);
//
//        } catch (Exception e) {
//            System.err.println("생년월일 변환 오류: " + e.getMessage());
//            memberEntity.setBirth(null);
//        }
//
//        List<String> hobbies = dto.getHobby();
//        if (hobbies != null && !hobbies.isEmpty()) {
//            memberEntity.setHobby(String.join(",", hobbies));
//        } else {
//            memberEntity.setHobby(null); // 또는 memberEntity.setHobby("");
//        }
//
//        try{
//            memberRepository.save(memberEntity);
//            return "redirect:/member/view?idx=" + memberEntity.getIdx();
//        }catch (Exception e) {
//            redirectAttributes.addFlashAttribute("msg", "수정오류");
//            redirectAttributes.addFlashAttribute("url", "/member/list");
//            return "redirect:/error_page";
//        }
//    }

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