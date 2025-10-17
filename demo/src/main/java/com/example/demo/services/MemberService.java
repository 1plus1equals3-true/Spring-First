package com.example.demo.services;

import com.example.demo.dto.JoinDTO;
import com.example.demo.dto.ListPageDTO;
import com.example.demo.dto.MemberViewDTO;

public interface MemberService {
    public boolean insert(JoinDTO dto);
    public ListPageDTO list(ListPageDTO dto);
    MemberViewDTO view(long idx);
    void delete(long idx);
    JoinDTO edit(long idx);
    void editProc(JoinDTO dto);
}
