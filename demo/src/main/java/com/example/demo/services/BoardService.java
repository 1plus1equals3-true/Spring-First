package com.example.demo.services;

import com.example.demo.dto.BoardDTO;

public interface BoardService {
    void write(BoardDTO dto, String clientIp);
}
