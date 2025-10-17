package com.example.demo.services;

import com.example.demo.dto.JoinDTO;
import com.example.demo.dto.ListPageDTO;
import com.example.demo.dto.MemberViewDTO;
import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Value("${file.upload.base-dir}")
    private String UPLOAD_BASE_DIR;

    @Override
    @Transactional
    public boolean insert(JoinDTO dto) {

        MultipartFile upfile = dto.getUpfile();
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
                return false;
            } catch (Exception e) {
                System.err.println("기타 파일 업로드 오류: " + e.getMessage());
                return false;
            }
        }

        MemberEntity memberEntity = dto.toEntity(dir, originalfile);
        memberRepository.save(memberEntity);

        return true;
    }

    @Override
    public ListPageDTO list(ListPageDTO dto) {
        System.out.println("---------------> MemberService list");

        int page = dto.getPage();
        String key = dto.getKey();
        String word = dto.getWord();

        int pageIndex = page - 1;
        final int PAGE_SIZE = 10;    // 한 페이지에 보여줄 리스트 수
        int blockLimit = 10;         // 페이징 수

        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.Direction.DESC, "idx");
        Page<MemberEntity> list = null;

        if (word == null || word.trim().isEmpty()) {
            list = memberRepository.findAll(pageable);
        } else {
            switch (key) {
                case "userid":
                    list = memberRepository.findByUseridContaining(word, pageable);
                    break;
                case "name":
                    list = memberRepository.findByNameContaining(word, pageable);
                    break;
                case "hobby":
                    list = memberRepository.findByHobbyContaining(word, pageable);
                    break;
                default: // 유효하지 않은 key인 경우 전체 검색
                    list = memberRepository.findAll(pageable);
                    break;
            }
        }

        int nowPage = page;
        int totalPages = list.getTotalPages();

        int startPage = 0;
        int endPage = 0;

        if (totalPages > 0) { // 0 페이지 처리
            startPage = (int)(Math.ceil(nowPage / (double)blockLimit) - 1) * blockLimit + 1;

            // 전체 페이지 수를 넘지 않도록
            endPage = Math.min(startPage + blockLimit - 1, totalPages);

            // startPage가 1보다 작아지지 않도록
            startPage = Math.max(1, startPage);
        }

        // 4. 조회 결과 및 페이지네이션 정보를 DTO에 담아 반환
        ListPageDTO listDto = ListPageDTO.builder()
                .memberList(list) // 페이지 데이터
                .nowPage(nowPage) // 현재 페이지
                .startPage(startPage) // 페이지 블록 시작 번호
                .endPage(endPage) // 페이지 블록 끝 번호
                .totalPages(totalPages) // 전체 페이지 수
                .key(key) // 검색 키 (응답으로 다시 전달)
                .word(word) // 검색어 (응답으로 다시 전달)
                .build();

        return listDto;
    }

    @Override
    public MemberViewDTO view(long idx) {
        System.out.println("---------------> MemberService view " + idx);

        // 데이터 조회 (Entity)
        MemberEntity memberEntity = memberRepository.findById(idx)
                .orElseThrow(() -> new NoSuchElementException("해당 idx(" + idx + ")의 회원을 찾을 수 없습니다."));

        // Entity를 DTO로 변환
        MemberViewDTO memberDto = MemberViewDTO.toEntity(memberEntity);

        // DTO 반환
        return memberDto;
    }

    @Override
    @Transactional
    public void delete(long idx) {
        System.out.println("---------------> MemberService delete: " + idx);

        MemberEntity memberEntity = memberRepository.findById(idx)
                .orElseThrow(() -> new NoSuchElementException("삭제할 회원 정보(idx: " + idx + ")를 찾을 수 없습니다."));

        // 첨부 파일이 있을 경우 파일 삭제 (오류 등이 발생해도 DB 삭제는 진행)
        String fileDir = memberEntity.getDir();
        if (fileDir != null && !fileDir.isEmpty()) {
            try {
                File attachedFile = new File(UPLOAD_BASE_DIR, fileDir);

                if (attachedFile.exists()) {
                    if (attachedFile.delete()) {
                        System.out.println("첨부파일 삭제 성공: " + fileDir);
                    } else {
                        System.err.println("첨부파일 삭제 실패: " + fileDir);
                    }
                } else {
                    System.out.println("첨부파일이 경로에 존재하지 않음: " + fileDir);
                }
            } catch (Exception e) {
                System.err.println("파일 삭제 중 오류 발생: " + e.getMessage());
            }
        }

        // 회원 정보 삭제
        memberRepository.deleteById(idx);
    }

    @Override
    public JoinDTO edit(long idx) {
        System.out.println("---------------> MemberService edit: " + idx);

        MemberEntity memberEntity = memberRepository.findById(idx)
                .orElseThrow(() -> new NoSuchElementException("수정할 회원 정보(idx: " + idx + ")를 찾을 수 없습니다."));

        LocalDate birth = memberEntity.getBirth();
        String yyyy = (birth != null) ? String.valueOf(birth.getYear()) : null;
        String mm = (birth != null) ? String.format("%02d", birth.getMonthValue()) : null;
        String dd = (birth != null) ? String.format("%02d", birth.getDayOfMonth()) : null;

        String hobbyStr = memberEntity.getHobby();
        java.util.List<String> hobbyList = (hobbyStr != null && !hobbyStr.isEmpty())
                ? Arrays.asList(hobbyStr.split(","))
                : null;

        JoinDTO dto = JoinDTO.builder()
                .idx(memberEntity.getIdx())
                .userid(memberEntity.getUserid())
                .pwd1(memberEntity.getPwd())
                .pwd2(memberEntity.getPwd())
                .name(memberEntity.getName())
                .gender(memberEntity.getGender())
                .yyyy(yyyy) // 확인 후 삭제
                .mm(mm)
                .dd(dd)
                .birth(memberEntity.getBirth())
                .hobby(hobbyList)
                .member_rank(memberEntity.getMemberRank())
                .originalfile(memberEntity.getOriginalfile())
                .dir(memberEntity.getDir())
                .build();

        return dto;
    }

    @Override
    @Transactional
    public void editProc(JoinDTO dto) {
        System.out.println("---------------> MemberService edit_proc");

        MemberEntity memberEntity = memberRepository.findById(dto.getIdx())
                .orElseThrow(() -> new NoSuchElementException("수정할 회원 정보(idx: " + dto.getIdx() + ")를 찾을 수 없습니다."));

        MultipartFile upfile = dto.getUpfile();
        String originalfile = memberEntity.getOriginalfile(); // 기존 파일명
        String dir = memberEntity.getDir();                   // 기존 파일 경로

        try {
            // deleteFile 체크박스 확인
            if (Boolean.TRUE.equals(dto.getDeleteFile())) {
                if (dir != null && !dir.isEmpty()) {
                    File oldFile = new File(UPLOAD_BASE_DIR, dir);
                    if (oldFile.exists()) {
                        oldFile.delete(); // 실제 파일 삭제
                    }
                }
                // DB 칼럼정보 삭제
                originalfile = null;
                dir = null;
            }

            // 업로드 처리
            if (upfile != null && !upfile.isEmpty()) {
                // 기존 파일 삭제
                if (dir != null && !dir.isEmpty()) {
                    File oldFile = new File(UPLOAD_BASE_DIR, dir);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }

                // 디렉토리 생성
                LocalDate today = LocalDate.now();
                String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String relativeDirName = "Spring" + dateString;
                String fullUploadPath = UPLOAD_BASE_DIR + relativeDirName;

                File directory = new File(fullUploadPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // 파일 이름 생성 및 저장
                String originalFilenameFromForm = upfile.getOriginalFilename();
                String extension = "";
                int dotIndex = originalFilenameFromForm.lastIndexOf(".");
                if (dotIndex > 0) {
                    extension = originalFilenameFromForm.substring(dotIndex);
                }

                String uuid = UUID.randomUUID().toString();
                String storedFilename = uuid + extension;

                // 파일 저장
                File dest = new File(fullUploadPath, storedFilename);
                upfile.transferTo(dest);

                // DB에 저장할 경로와 이름 업데이트
                originalfile = originalFilenameFromForm;
                dir = relativeDirName + "/" + storedFilename;
            }
            // 새 파일도 없고 삭제 요청도 없는 경우, 기존 값 유지

        } catch (IOException e) {
            System.err.println("파일 처리 중 I/O 오류 발생: " + e.getMessage());
            throw new RuntimeException("파일 저장 또는 삭제 오류가 발생했습니다.", e);
        }

        memberEntity.setOriginalfile(originalfile);
        memberEntity.setDir(dir);

        // 비밀번호 공백이면 수정하지않음
        if (dto.getPwd1() != null && !dto.getPwd1().isEmpty()) {
            memberEntity.setPwd(dto.getPwd1());
        }

        memberEntity.setName(dto.getName());
        memberEntity.setGender(dto.getGender());

        try {
            LocalDate birthDate = LocalDate.of(
                    Integer.parseInt(dto.getYyyy()),
                    Integer.parseInt(dto.getMm()),
                    Integer.parseInt(dto.getDd())
            );
            memberEntity.setBirth(birthDate);

        } catch (Exception e) {
            System.err.println("생년월일 변환 오류: " + e.getMessage());
            // 생년월일 파싱 오류는 RuntimeException으로 처리
            throw new RuntimeException("생년월일 형식 오류: " + e.getMessage(), e);
        }

        List<String> hobbies = dto.getHobby();
        if (hobbies != null && !hobbies.isEmpty()) {
            memberEntity.setHobby(String.join(",", hobbies));
        } else {
            memberEntity.setHobby(null);
        }

        memberRepository.save(memberEntity);
    }
}