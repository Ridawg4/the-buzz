package com.teamwhite.thebuzz.Contexts;

import com.teamwhite.thebuzz.Repositories.BuzzSessionRepository;
import com.teamwhite.thebuzz.Repositories.SessionInfoRepository;
import com.teamwhite.thebuzz.Services.SessionInfoService;
import com.teamwhite.thebuzz.Services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@Configuration
@EnableSpringHttpSession
public class SessionConfiguration {

    /**
     * Tells Spring Session to use the custom BuzzUserSessionRepository
     * @param sessionRepository Autowired
     * @return BuzzUserSessionRepository session repository
     */
    @Bean
    public BuzzSessionService sessionRepository(BuzzSessionRepository sessionRepository, UserService userService, SessionInfoService sessionInfoService) {
        return new BuzzSessionService(sessionRepository, userService, sessionInfoService);
    }
}
