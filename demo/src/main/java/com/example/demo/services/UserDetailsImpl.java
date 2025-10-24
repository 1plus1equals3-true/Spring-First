package com.example.demo.services;

import com.example.demo.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private MemberEntity memberEntity;

    public UserDetailsImpl(MemberEntity memberEntity)
    {
        this.memberEntity = memberEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (memberEntity.getMemberRank() >= 9) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return memberEntity.getPwd();
    }

    @Override
    public String getUsername() {
        return memberEntity.getUserid();
    }

    public String getName() {
        return memberEntity.getName();
    }
}
