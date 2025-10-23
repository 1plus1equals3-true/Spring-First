package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/idCheck").permitAll()
                        .requestMatchers("/member/join","/member/join_proc").permitAll()
                        .requestMatchers("/member/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .anyRequest().authenticated());

        http.formLogin(formLogin -> formLogin
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login_proc")
                        .usernameParameter("userid")
                        .passwordParameter("pwd")
                        .defaultSuccessUrl("/board/list")
                        .failureUrl("/member/login?error"));

        http.logout(logout -> logout
                .logoutUrl("/member/logout") // 로그아웃 URL 지정
                .logoutSuccessUrl("/member/login") // 로그아웃 성공 후 리다이렉트할 URL
                .invalidateHttpSession(true) // 세션 무효화
                .clearAuthentication(true) // 인증 정보 삭제
                .deleteCookies("JSESSIONID") // 쿠키 삭제
                .permitAll() // 모든 사용자에게 로그아웃 URL 접근 허용
        );

        return http.build();
    }
}
