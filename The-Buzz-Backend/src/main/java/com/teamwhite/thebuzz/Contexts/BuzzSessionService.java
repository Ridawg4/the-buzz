package com.teamwhite.thebuzz.Contexts;

import com.teamwhite.thebuzz.Repositories.BuzzSessionRepository;
import com.teamwhite.thebuzz.Repositories.SessionInfoRepository;
import com.teamwhite.thebuzz.Repositories.UserRepository;
import com.teamwhite.thebuzz.Services.SessionInfoService;
import com.teamwhite.thebuzz.Services.UserService;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.SessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Security;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Repository
@Primary
public class BuzzSessionService implements SessionRepository<BuzzUserSession> {
    private final BuzzSessionRepository repository;
    private final SessionInfoService sessionInfoService;
    private final UserService userService;

    /**
     * Autowired constructor
     * @param repository BuzzSessionRepository reference
     */
    public BuzzSessionService(BuzzSessionRepository repository, UserService userService, SessionInfoService sessionInfoService) {
        this.repository = repository;
        this.userService = userService;
        this.sessionInfoService = sessionInfoService;
    }

    /**
     * Creates a new BuzzUserSession without specified SessionPermissions
     * @hidden I wish it would allow me to include SessionPermissions at runtime,
     * but since it's an implemented method, I cannot do that and runtime won't recognize it if I did
     * @return BuzzUserSession created session
     */
    @Override
    public BuzzUserSession createSession() {
        return new BuzzUserSession();
    }

    /**
     * Saves the session into persistent storage or updates persistent storage
     * if the session already exists
     * @param session the {@link org.springframework.session.Session} to save
     */
    @Override
    public void save(BuzzUserSession session) {
        if(repository.getSessionById(session.getId()) == null) {
            repository.addSession(session.getId(), session.getCreationTimeAsTimestamp(), session.getLastAccessedTimeAsTimestamp(), session.getPermissions().asJsonObject().toString(), session.getAllAttributs().toString());
            SecurityContextImpl context = null;

            try {
                context = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            } catch (NullPointerException nullValue) {

            }
            BuzzUser userOnAuth = null;

            try {
                userOnAuth = (BuzzUser) context.getAuthentication().getPrincipal();
                sessionInfoService.addSessionInfo(session.getId(), userOnAuth.getUserId(), false);
            } catch (ClassCastException | NullPointerException classCast) {
            }
        } else {
            repository.updateSession(session.getId(), session.getCreationTimeAsTimestamp(), session.getLastAccessedTimeAsTimestamp(), session.getPermissions().asJsonObject().toString(), session.getAllAttributs().toString());
        }
    }

    /**
     * Gets the {@link BuzzUserSession} with the associated ID,
     * or null if the value does not exist
     * @param id the {@link org.springframework.session.Session#getId()} to lookup
     * @return BuzzUserSession the session with the ID provided
     */
    @Override
    public BuzzUserSession findById(String id) {
        if(!id.isEmpty()) {
            BuzzUserSession retrievedSession = repository.getSessionById(id);

            SessionInfo info = sessionInfoService.getSessionInfoBySessionId(id);

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            HttpServletRequest request;
            HttpServletResponse response;
            try {
                request = attributes.getRequest();
                response = attributes.getResponse();
            } catch (NullPointerException nullPointer) {
                return retrievedSession;
            }

            if(info == null) {
                return retrievedSession;
            }

            if (info.isNeedsRefresh()) {
                SecurityContextImpl context = null;

                try {
                    context = retrievedSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                } catch (NullPointerException nullValue) {

                }
                BuzzUser userOnAuth = null;

                try {
                    userOnAuth = (BuzzUser) context.getAuthentication().getPrincipal();
                } catch (ClassCastException | NullPointerException classCast) {

                }

                renewCurrentUserSession(userOnAuth.getEmail(), retrievedSession, context.getAuthentication(), request, response);
            }
            return retrievedSession;
        } else {
            // @TODO Throw an error if id is null, not return null
            return null;
        }
    }

    /**
     * Deletes the session with the given ID from persistent storage
     * @param id the {@link org.springframework.session.Session#getId()} to delete
     */
    @Override
    public void deleteById(String id) {
        repository.deleteSession(id);
        sessionInfoService.deleteSessionInfo(id);
    }

    /**
     * Labels all sessions for a given user that they need to renew authentication
     * @param userId String userId we want to refresh sessions for
     * @param auth Authentication the current authentication
     * @param request HttpServletRequest autofilled
     * @param response HttpServletResponse autofilled
     */
    public void labelAllSessionsForUserToRenew(String userId, Authentication auth, HttpServletRequest request, HttpServletResponse response) {
        SessionInfo[] sessionInfos = sessionInfoService.getAllSessionsInfoByOwnerId(userId);

        if(sessionInfos != null) {
            for(SessionInfo info : sessionInfos) {
                try {
                    sessionInfoService.updateNeedsRefreshBySessionId(info.getId(), true);
                } catch( Exception e) {
                    Logger.getGlobal().warning(e.getMessage());
                }
            }
        }
    }

    /**
     * Renews the current session associated with a user
     * @param email String the email associated with the user account
     * @param session BuzzUserSession the current session
     * @param auth Authentication the current authentication object
     * @param request HttpServletRequest autofilled
     * @param response HttpServletResponse autofilled
     */
    public void renewCurrentUserSession(String email, BuzzUserSession session, Authentication auth, HttpServletRequest request, HttpServletResponse response) {
        BuzzUser updatedUser = userService.getUserRecordByEmail(email);

        Authentication renewedAuth;
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        renewedAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                auth.getCredentials(),
                updatedUser.getAuthorities()
        );

        BuzzUser test = (BuzzUser) renewedAuth.getPrincipal();
        context.setAuthentication(renewedAuth);
        SecurityContextHolder.setContext(context);

        /**
         * This is ridiculous, why do i need to do this
         */
        session.setAttribute(HttpSessionSecurityContextRepository
                .SPRING_SECURITY_CONTEXT_KEY, context);
        sessionInfoService.updateNeedsRefreshBySessionId(session.getId(), false);
    }
}
