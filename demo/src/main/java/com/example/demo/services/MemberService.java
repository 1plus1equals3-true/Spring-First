package com.example.demo.services;

import com.example.demo.dto.JoinDTO;
import com.example.demo.dto.MemberListDTO;
import com.example.demo.dto.MemberViewDTO;

public interface MemberService {
    public boolean insert(JoinDTO dto);
    public MemberListDTO list(MemberListDTO dto);
    MemberViewDTO view(long idx);
    void delete(long idx);
    JoinDTO edit(long idx);
    void editProc(JoinDTO dto);
}
