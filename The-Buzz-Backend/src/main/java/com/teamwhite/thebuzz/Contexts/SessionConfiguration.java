package com.teamwhite.thebuzz.Contexts;

import com.teamwhite.thebuzz.Repositories.BuzzSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public BuzzUserSessionRepository sessionRepository(BuzzSessionRepository sessionRepository) {
        return new BuzzUserSessionRepository(sessionRepository);
    }
}
