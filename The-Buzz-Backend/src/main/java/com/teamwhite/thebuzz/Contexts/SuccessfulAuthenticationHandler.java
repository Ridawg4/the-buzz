package com.teamwhite.thebuzz.Contexts;

import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.json.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SuccessfulAuthenticationHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(200);
        BuzzUser authenticatedUser = (BuzzUser) authentication.getPrincipal();
        assert authenticatedUser != null;
        response.getOutputStream().print(authenticatedUser.getUserId());
        response.setContentLength(authenticatedUser.getUserId().length());
    }
}
