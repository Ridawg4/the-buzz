package com.teamwhite.thebuzz.Contexts;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Required to avoid circular linking dependencies
 * Likely can be refactored so this file can be deleted
 */
@Component
public class Beans {
    /**
     * Password encoder for the application
     * @return BCryptPasswordEncoder password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
