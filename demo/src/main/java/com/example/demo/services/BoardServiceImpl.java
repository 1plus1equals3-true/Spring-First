package com.example.demo.services;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.ListPageDTO;
import com.example.demo.entity.BoardAttachmentEntity;
import com.example.demo.entity.BoardEntity;
import com.example.demo.repository.BoardAttachmentRepository;
import com.example.demo.repository.BoardRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
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

    @Override
    public ListPageDTO list(ListPageDTO dto) {
        System.out.println("---------------> BoardService board list");

        int page = dto.getPage();
        String key = dto.getKey();
        String word = dto.getWord();

        int pageIndex = page - 1;
        final int PAGE_SIZE = 10;    // 한 페이지에 보여줄 리스트 수
        int blockLimit = 10;         // 페이징 수

        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.Direction.DESC, "idx");
        Page<BoardEntity> list = null;

        if (word == null || word.trim().isEmpty()) {
            list = boardRepository.findAll(pageable);
        } else {
            switch (key) {
                case "name":
                    list = boardRepository.findByNameContaining(word, pageable);
                    break;
                case "title":
                    list = boardRepository.findByTitleContaining(word, pageable);
                    break;
                case "content":
                    list = boardRepository.findByContentContaining(word, pageable);
                    break;
                default: // 유효하지 않은 key인 경우 전체 검색
                    list = boardRepository.findAll(pageable);
                    break;
            }
        }

        int nowPage = page;
        int totalPages = list.getTotalPages();
        long totalElements = list.getTotalElements(); // 전체 게시글 수

        long startNumber = totalElements - ((long)(nowPage - 1) * PAGE_SIZE);
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
                .boardList(list)            // 페이지 데이터
                .nowPage(nowPage)           // 현재 페이지
                .startPage(startPage)       // 페이지 블록 시작 번호
                .endPage(endPage)           // 페이지 블록 끝 번호
                .totalPages(totalPages)     // 전체 페이지 수
                .key(key)
                .word(word)
                .startNumber(startNumber)   // 글번호
                .build();

        return listDto;
    }

    @Override
    public BoardDTO view(long idx) {
        System.out.println("---------------> BoardService view " + idx);

        // 데이터 조회
        BoardEntity boardEntity = boardRepository.findById(idx)
                .orElseThrow(() -> new NoSuchElementException("해당 idx(" + idx + ")의 게시글을 찾을 수 없습니다."));

        // Entity를 DTO로 변환
        BoardDTO boardDTO = BoardDTO.builder()
                .idx(idx)
                .title(boardEntity.getTitle())
                .name(boardEntity.getName())
                .regDate(boardEntity.getRegdate())
                .hit(boardEntity.getHit())
                .ip(boardEntity.getIp())
                .content(boardEntity.getContent())
                .build();

        // DTO 반환
        return boardDTO;
    }

    @Override
    @Transactional
    public void delete(long idx) {
        System.out.println("---------------> BoardService delete: " + idx);

        BoardEntity boardEntity = boardRepository.findById(idx)
                .orElseThrow(() -> new NoSuchElementException("삭제할 게시글 정보(idx: " + idx + ")를 찾을 수 없습니다."));

        // 연결된 첨부파일 목록 조회
        List<BoardAttachmentEntity> attachments = boardAttachmentRepository.findByBidx(idx);

        // 파일 삭제
        if (!attachments.isEmpty()) {
            for (BoardAttachmentEntity attachment : attachments) {
                String fileDir = attachment.getDir(); // BoardAttachmentEntity에서 경로를 가져옴

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
                    // 파일 삭제 중 오류가 발생하더라도 게시글 삭제는 계속 진행
                    System.err.println("파일 삭제 중 오류 발생: " + e.getMessage());
                }
            }

            boardAttachmentRepository.deleteAll(attachments); // 조회된 리스트를 삭제
        }

        boardRepository.deleteById(idx);
    }

    @Override
    public BoardDTO modify(long idx) {
        System.out.println("---------------> BoardService modify: " + idx);

        BoardEntity boardEntity = boardRepository.findById(idx)
                .orElseThrow(() -> new NoSuchElementException("수정할 게시글(idx: " + idx + ")을 찾을 수 없습니다."));

        BoardDTO dto = BoardDTO.builder()
                .idx(idx)
                .ip(boardEntity.getIp())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .build();

        return dto;
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