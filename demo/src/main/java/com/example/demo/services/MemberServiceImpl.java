package com.example.demo.services;

import com.example.demo.dto.JoinDTO;
import com.example.demo.dto.MemberListDTO;
import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Value("${file.upload.base-dir}")
    private String UPLOAD_BASE_DIR;

    @Override
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
    public MemberListDTO list(MemberListDTO dto) {
        //리스트 처리하는곳
        return null;
    }
}