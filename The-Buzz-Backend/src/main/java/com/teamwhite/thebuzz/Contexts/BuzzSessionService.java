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

            // Sessions need to also create a SessionInfo for themselves, or else it breaks
            SecurityContextImpl context = null;
            try {
                // Pulls the SecurityContextImpl from the session
                context = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            } catch (NullPointerException exception) {
//                Logger.getGlobal().info("Session does not have SecurityContextImpl");
            }
            BuzzUser userAuth = null;

            try {
                userAuth = (BuzzUser) context.getAuthentication().getPrincipal();
            } catch (ClassCastException | NullPointerException exception) {
                // Delete any sessions that are generated that are not needed
                // This does break redirects after auth, but those are broken right now anyway
                deleteById(session.getId());
            }

            if(userAuth != null) {
                // If the user exists, then userAuth.getId will always have a value
                sessionInfoService.addSessionInfo(session.getId(), userAuth.getUserId(), true);
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

            // If no SessionInfo exists, simply give the current session
            if(info == null) {
                return retrievedSession;
            }

            // If the SessionInfo declares it needs a refresh, grab information and refresh session
            if (info.isNeedsRefresh()) {
                SecurityContextImpl context = null;
                // Gets the HttpServletRequest and HttpServletResponse objects in the request
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                HttpServletRequest request;
                HttpServletResponse response;

                try {
                    request = attributes.getRequest();
                    response = attributes.getResponse();
                } catch (NullPointerException exception) {
                    // If no request or response exist, something weird happened
                    Logger.getGlobal().warning("Session refresh has an error: " + exception.getMessage());
                    return retrievedSession;
                }

                try {
                    context = retrievedSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                } catch (NullPointerException nullValue) {
                    // If no SecurityContext exists in the session, simply return the session
                    return retrievedSession;
                }
                BuzzUser userOnAuth = null;

                try {
                    userOnAuth = (BuzzUser) context.getAuthentication().getPrincipal();
                } catch (ClassCastException | NullPointerException exception) {
                    // If the principal is not a BuzzUser, then something weird happened here too
                    Logger.getGlobal().warning("Session refreshing has an error: " + exception.getMessage());
                    return retrievedSession;
                }

                // Refresh the current user's session
                // We use userId, as the email can be changed, which can be why the refresh is needed
                // Therefore, using the old email will return with a error
                renewCurrentUserSession(userOnAuth.getUserId(), retrievedSession, context.getAuthentication(), request, response);
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
     * @param userId String the userId associated with the user account
     * @param session BuzzUserSession the current session
     * @param auth Authentication the current authentication object
     * @param request HttpServletRequest autofilled
     * @param response HttpServletResponse autofilled
     */
    public void renewCurrentUserSession(String userId, BuzzUserSession session, Authentication auth, HttpServletRequest request, HttpServletResponse response) {
        // Pulls the most up-to-date user from the database
        BuzzUser updatedUser = userService.getUserRecordByUserId(userId);

        Authentication renewedAuth;
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // This creates a new Authentication token, which is where our user object is stored
        renewedAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                // Might create issues if these are updated as well
                auth.getCredentials(),
                updatedUser.getAuthorities()
        );

        // Sets our SecurityContext to the newly created Authentication token
        context.setAuthentication(renewedAuth);
        // Puts our SecurityContext into the system's SystemContextHolder
        SecurityContextHolder.setContext(context);

        // This is ridiculous, why do I need to do it this way
        // Manually update the attribute in the session attribute with the new Authentication token
        session.setAttribute(HttpSessionSecurityContextRepository
                .SPRING_SECURITY_CONTEXT_KEY, context);
        // Tells the session it no longer needs to refresh in the database
        sessionInfoService.updateNeedsRefreshBySessionId(session.getId(), false);
    }
}
