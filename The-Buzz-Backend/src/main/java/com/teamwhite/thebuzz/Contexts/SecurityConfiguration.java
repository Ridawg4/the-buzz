package com.teamwhite.thebuzz.Contexts;

import com.teamwhite.thebuzz.Services.BuzzUserDetailsManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug=false)
class SecurityConfiguration {
    private final BuzzUserDetailsManager buzzUserDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfiguration(BuzzUserDetailsManager buzzUserDetailsManager, PasswordEncoder passwordEncoder) {
        this.buzzUserDetailsManager = buzzUserDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.sessionManagement(httpSecuritySessionManagementConfigurer -> {
                    httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                })
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.dispatcherTypeMatchers().permitAll()
                    .requestMatchers("/api/v1/**", "/login.html", "/signup.html")
                            .permitAll()
                            .requestMatchers("/upload.html")
                            .hasAnyAuthority("CAN_UPLOAD_LIVE_STREAM_SEGMENTS", "CAN_UPLOAD_PRERECORDED_SHOW_AUDIO")
                    .anyRequest().authenticated();
                }).authenticationManager(authenticationManager(httpSecurity))
                .formLogin((formLogin) -> {
                    formLogin.loginPage("/login.html")
                            .loginProcessingUrl("/api/v1/login")
                            .defaultSuccessUrl("/upload.html")
                            .permitAll();
                })
                .logout(logout -> {
                    logout.clearAuthentication(true)
                            .deleteCookies("SESSION", "JSESSIONID")
                            .logoutUrl("/api/v1/logout")
                            .logoutSuccessUrl("/loginPage.html")
                            .permitAll();
                });

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(buzzUserDetailsManager).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}
