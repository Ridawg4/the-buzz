package com.teamwhite.thebuzz.Contexts;

import com.google.common.net.HttpHeaders;
import com.teamwhite.thebuzz.Services.AccountModerationService;
import com.teamwhite.thebuzz.model.AccountModeration;
import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SuccessfulAuthenticationHandler implements AuthenticationSuccessHandler {

    private final AccountModerationService accountModerationService;
    private final BuzzSessionService sessionService;

    public SuccessfulAuthenticationHandler(AccountModerationService accountModerationService, BuzzSessionService sessionService) {
        this.accountModerationService = accountModerationService;
        this.sessionService = sessionService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        BuzzUser authenticatedUser = (BuzzUser) authentication.getPrincipal();
        assert authenticatedUser != null;
        // Returns an array with the first index being the bool result and the second
        // being the array of values, if one exists
        Object[] unAcknowledgedActionsResult = hasUnacknowledgedAccountAction(authenticatedUser);

        if((boolean) unAcknowledgedActionsResult[0]) {
            denyAuthForOutstandingAccountActions(request, response, authentication, unAcknowledgedActionsResult, authenticatedUser);
            sessionService.removeAllSessionsByUserId(authenticatedUser.getUserId());
        } else {
            response.setStatus(200);
            response.getOutputStream().print(authenticatedUser.getUserId());
            response.setContentLength(authenticatedUser.getUserId().length());
        }
    }

    private Object[] hasUnacknowledgedAccountAction(BuzzUser currUser) {
        AccountModeration[] actions = accountModerationService.getUnacknowledgedActionsByUserId(currUser.getUserId());

        if(actions == null) {
            return new Object[] {false, null};
        }
        if(actions.length != 0) {
            return new Object[] {true, actions};
        }
        return new Object[] {false, null};
    }

    private void denyAuthForOutstandingAccountActions(HttpServletRequest request, HttpServletResponse response, Authentication authentication, Object[] unAcknowledgedActionsResult, BuzzUser authenticatedUser) throws IOException {
        AccountModeration[] actions = (AccountModeration[]) unAcknowledgedActionsResult[1];
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();

        for(AccountModeration actionEntry : actions) {
            arrBuilder.add(actionEntry.asJsonObject().toString());
        }

        builder.add("error_code", "UNACK-ACC-ACTIONS")
                .add("error", "Account has unacknowledged moderation actions")
                .add("actions", arrBuilder.build())
                .add("userId", authenticatedUser.getUserId());

        String message = builder.build().toString();
        ResponseCookie deleteSession = ResponseCookie.from("SESSION", "")
                .path("/")
                .maxAge(0)
                .secure(true)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteSession.toString());
        response.setStatus(403);
        response.getOutputStream().print(message);
        response.setContentLength(message.length());
    }
}
