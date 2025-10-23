package com.example.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        System.out.println("======= Authentication authenticate=====");

        String userid = authentication.getPrincipal().toString();
        String pwd = authentication.getCredentials().toString();

        System.out.println(">>>>> userid: " + userid);
        System.out.println(">>>>> pwd: " + pwd);

        UserDetails user = userDetailsService.loadUserByUsername(userid);

        System.out.println(user);

        if(pwd.equals(user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userid, pwd, user.getAuthorities());
        } else {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
