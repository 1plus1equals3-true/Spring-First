package com.example.demo.services;

import com.example.demo.dto.JoinDTO;
import com.example.demo.dto.MemberListDTO;

public interface MemberService {
    public boolean insert(JoinDTO dto);
    public MemberListDTO list(MemberListDTO dto);
}
