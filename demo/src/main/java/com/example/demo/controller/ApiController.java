package com.example.demo.controller;

import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final MemberRepository memberRepository;

    @GetMapping("/idCheck")
    public ResponseEntity<String> idCheck(@RequestParam("userid") String userid) {

        MemberEntity member = memberRepository.findByUserid(userid);

        if (member != null) {
            return ResponseEntity.ok("n"); // 중복
        } else {
            return ResponseEntity.ok("y"); // 사용 가능
        }
    }

    //dto 이용한 json형태로 받는법
//    @GetMapping("/idCheck")
//
//    public ResponseEntity<joinDTO> idCheck(@RequestParam("userid") String userid) {
//
//        MemberEntity member = memberRepository.findByUserid(userid);
//
//
//        joinDTO responseDto = new joinDTO();
//
//        if (member != null) {
//            responseDto.setIdCheckResult("n");
//        } else {
//            responseDto.setIdCheckResult("y");
//        }
//        return ResponseEntity.ok(responseDto);
//    }

}
