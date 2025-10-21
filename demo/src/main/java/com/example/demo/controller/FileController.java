package com.example.demo.controller;

import com.example.demo.entity.BoardAttachmentEntity;
import com.example.demo.repository.BoardAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final BoardAttachmentRepository boardAttachmentRepository;

    @Value("${file.upload.base-dir}")
    private String UPLOAD_BASE_DIR;

    /**
     * 첨부파일을 다운로드하거나 img 태그로 표시하기 위해 파일을 제공하는 메서드
     * @param idx BoardAttachmentEntity의 고유 ID (파일 ID)
     * @return 파일 데이터와 HTTP 헤더를 담은 ResponseEntity
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("idx") long idx) {

        // 1. 파일 엔티티 조회
        Optional<BoardAttachmentEntity> optionalFile = boardAttachmentRepository.findById(idx);
        if (!optionalFile.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "첨부파일을 찾을 수 없습니다.");
        }
        BoardAttachmentEntity fileEntity = optionalFile.get();

        String fileDir = fileEntity.getDir(); // 저장된 상대 경로 (예: Spring20231021/uuid.jpg)
        String fullPath = UPLOAD_BASE_DIR + fileDir; // 전체 경로

        // 2. 파일 시스템에서 리소스 로드
        Path path = Paths.get(fullPath);
        Resource resource = new FileSystemResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일 리소스를 찾거나 읽을 수 없습니다.");
        }

        try {
            // 3. 파일명 인코딩 및 Content-Type 설정
            String originalFilename = fileEntity.getOriginalfile();
            String encodedFilename = UriUtils.encode(originalFilename, StandardCharsets.UTF_8);

            // 파일의 실제 MIME 타입 확인
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream"; // 기본값 (다운로드용)
            }

            // 4. Content-Disposition 헤더 설정
            // Content-Disposition: inline -> 브라우저가 표시할 수 있으면 표시 (img, pdf 등)
            // Content-Disposition: attachment -> 브라우저가 무조건 다운로드 (zip, exe 등)

            String disposition;
            if (contentType.startsWith("image/") || contentType.startsWith("video/") || contentType.startsWith("application/pdf")) {
                // 이미지, 비디오, PDF 등은 인라인으로 처리하여 브라우저에 표시 시도
                disposition = "inline; filename=\"" + encodedFilename + "\"";
            } else {
                // 그 외의 파일은 다운로드 처리
                disposition = "attachment; filename=\"" + encodedFilename + "\"";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, disposition);
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류 발생", e);
        }
    }
}