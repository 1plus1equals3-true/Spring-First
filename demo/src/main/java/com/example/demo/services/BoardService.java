package com.example.demo.services;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.ListPageDTO;

public interface BoardService {
    void write(BoardDTO dto, String clientIp);
    public ListPageDTO list(ListPageDTO dto);
    BoardDTO view(long idx);
    void delete(long idx);
    BoardDTO modify(long idx);
    void modifyProc(BoardDTO dto);
}
