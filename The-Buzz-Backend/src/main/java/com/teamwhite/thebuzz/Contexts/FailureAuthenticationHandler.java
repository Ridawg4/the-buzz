package com.teamwhite.thebuzz.Contexts;

import com.google.common.net.HttpHeaders;
import jakarta.json.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class FailureAuthenticationHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        String message = Json.createObjectBuilder().add("error", exception.getMessage()).build().toString();
        ResponseCookie deleteSession = ResponseCookie.from("SESSION", "")
                .path("/")
                .maxAge(0)
                .secure(true)
                .httpOnly(true)
                .sameSite("Lax")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteSession.toString());
        response.getOutputStream().print(message);
        response.setContentLength(message.length());
    }
}
