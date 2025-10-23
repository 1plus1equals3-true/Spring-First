package com.example.demo.services;

import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("UserDetails loadUserByUsername(String username)");
        System.out.println(">>>>>>>> "+ username);

        MemberEntity memberEntity = memberRepository.findByUserid(username);

        if(memberEntity == null)
        {
            throw new UsernameNotFoundException(username + " 사용자를 찾을 수 없습니다.");
        }
        else {
            return new UserDetailsImpl(memberEntity);
        }
    }
}
