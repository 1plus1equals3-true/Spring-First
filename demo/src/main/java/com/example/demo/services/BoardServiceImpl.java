package com.example.demo.services;

import com.example.demo.dto.BoardDTO;
import com.example.demo.entity.BoardAttachmentEntity;
import com.example.demo.entity.BoardEntity;
import com.example.demo.repository.BoardAttachmentRepository;
import com.example.demo.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardAttachmentRepository boardAttachmentRepository;

    @Value("${file.upload.base-dir}")
    private String UPLOAD_BASE_DIR;

    @Override
    @Transactional
    public void write(BoardDTO dto, String clientIp) {
        System.out.println("---------------> BoardService write_proc");

        BoardEntity boardEntity = new BoardEntity();

        boardEntity.setName(dto.getName());
        boardEntity.setPwd(dto.getPwd());
        boardEntity.setTitle(dto.getTitle());
        boardEntity.setContent(dto.getContent());

        boardEntity.setRegdate(LocalDateTime.now());
        boardEntity.setIp(clientIp);
        boardEntity.setBoardtype(2L); // 나중에 변수로 받거나해서 수정

        BoardEntity savedBoard = boardRepository.save(boardEntity);
        long newBoardIdx = savedBoard.getIdx();

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            // 빈 파일을 걸러내고, 유효한 파일만 리스트로 만듭니다.
            List<MultipartFile> validFiles = dto.getFiles().stream()
                    .filter(file -> !file.isEmpty())
                    .collect(Collectors.toList());
            // 파일 저장 메서드 호출
            if (!validFiles.isEmpty()) {

                if (validFiles.size() > 5) {
                    throw new RuntimeException("첨부 파일은 최대 5개까지만 허용됩니다.");
                }
                // 저장프로세스 호출, 5개가 넘으면 롤백
                processAttachments(newBoardIdx, validFiles);
            }
        }
    }

    private void processAttachments(long bidx, List<MultipartFile> files) {
        LocalDate today = LocalDate.now();
        String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativeDirName = "Spring" + dateString;
        String fullUploadPath = UPLOAD_BASE_DIR + relativeDirName;

        File directory = new File(fullUploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 넘어온 files 리스트 전체 순회
        for (MultipartFile upfile : files) {
            if (upfile.isEmpty()) continue;

            // 파일 정보 추출 및 저장
            String originalFilename = upfile.getOriginalFilename();
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }

            String uuid = UUID.randomUUID().toString();
            String storedFilename = uuid + extension;
            String fileDir = relativeDirName + "/" + storedFilename;

            try {
                // 실제 파일 저장
                File dest = new File(fullUploadPath, storedFilename);
                upfile.transferTo(dest);

                // BoardAttachmentEntity 생성 및 DB 저장
                BoardAttachmentEntity attachmentEntity = BoardAttachmentEntity.builder()
                        .bidx(bidx)
                        .originalfile(originalFilename)
                        .dir(fileDir)
                        .build();

                boardAttachmentRepository.save(attachmentEntity);

            } catch (IOException e) {
                System.err.println("첨부 파일 저장 중 I/O 오류 발생: " + e.getMessage());
                throw new RuntimeException("첨부 파일 저장 중 오류가 발생했습니다.", e);
            }
        }
    }
}