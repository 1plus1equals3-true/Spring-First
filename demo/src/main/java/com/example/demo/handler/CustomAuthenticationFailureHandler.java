package com.example.demo.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String msg = exception.getMessage();
        String encodedMsg = URLEncoder.encode(msg, StandardCharsets.UTF_8.toString());

        String fixedUrl = "/member/login";
        String encodedUrl = URLEncoder.encode(fixedUrl, StandardCharsets.UTF_8.toString());

        response.sendRedirect("/error_page?msg=" + encodedMsg + "&url=" + encodedUrl);

    }
}
