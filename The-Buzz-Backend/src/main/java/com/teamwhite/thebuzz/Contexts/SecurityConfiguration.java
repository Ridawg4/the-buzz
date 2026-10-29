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
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.session.Session;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * The Security Configuration that all endpoints follow unless annotated separately
 */
@Configuration
@EnableWebSecurity(debug=false)
class SecurityConfiguration {
    private final BuzzUserDetailsManager buzzUserDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource;

    /**
     * Autowired
     * @param buzzUserDetailsManager
     * @param passwordEncoder
     */
    public SecurityConfiguration(BuzzUserDetailsManager buzzUserDetailsManager, PasswordEncoder passwordEncoder, UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource) {
        this.buzzUserDetailsManager = buzzUserDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.urlBasedCorsConfigurationSource = urlBasedCorsConfigurationSource;
    }

    /**
     * Security chain that the application uses for authentication and authorization rules
     * @param httpSecurity HttpSecurity object | autowired
     * @return SecurityFilterChain chain we created
     * @throws Exception If the creation fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        // Sets how sessionManagement works
        httpSecurity.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            // Should only create a session if its required, but it considers every endpoint
            // as needing one, so I don't know how it's different
                    httpSecuritySessionManagementConfigurer
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                })
                // Disables cross site request forgery for all endpoints
                // This is not the correct solution, but makes testing easier
                // @TODO FIX CSRF POLICY
                .csrf(csrf -> csrf.disable())
                // @TODO REINSTANTE CORS POLICY
                .cors(cors -> cors.configurationSource(urlBasedCorsConfigurationSource))
                /**
                 * This is where we determine what is required to access an endpoint
                 * To map a specific endpoint, use .requestMathers() with a string value of your endpoint,
                 * starting from the root of our application:
                 * (The address for testing is "localhost:8080/", root refers to the "/"
                 * So, if we are securing the endpoint "localhost:8080/test.html", we enter "/test.html")
                 *
                 * Afterward, you can use .hasAuthority(), .hasAnyAuthority(), .permitAll(), or
                 * .authenticated() to restrict access to that endpoint
                 *
                 * .permitAll() allows anyone to access that endpoint, regardless of authentication or
                 * authorization provided
                 *
                 * .authenticated() allows anyone authenticated to access that endpoint, regardless of
                 * authorization provided
                 *
                 * .hasAnyAuthority() only allows those authenticated and has at least one of the
                 * permissions listed as Strings to access that endpoint
                 *
                 * .hasAuthority() only allows those authenticated and has a specific permission listed
                 * as a String to access that endpoint
                 *
                 * This is also documented under "Using Authentication And Authorization" that will
                 * be made by class
                 */
                .authorizeHttpRequests(auth -> {
                    auth.dispatcherTypeMatchers().permitAll()
                    .requestMatchers("/login.html", "/signup.html", "/api/v1/signup")
                            .permitAll()
                            .requestMatchers("/upload.html")
                            .permitAll()
                    .anyRequest().authenticated();
                }).authenticationManager(authenticationManager(httpSecurity))
                // Maps our authentication pipeline to use Form Login, redirect after successful login,
                // and permit all users to access those endpoints
                .formLogin((formLogin) -> {
                    formLogin.loginPage("/login.html")
                            .loginProcessingUrl("/api/v1/login")
//                            .defaultSuccessUrl("/accountDetails.html")
                            .successHandler(new SuccessfulAuthenticationHandler())
                            .failureHandler(new FailureAuthenticationHandler())
                            .permitAll();
                })
                // Maps the logout authentication to clear the cookies from the browser,
                // clear the Authentication object, and redirect to the login url
                .logout(logout -> {
                    logout.clearAuthentication(true)
                            // @TODO MAKE SURE COOKIES LEAVE
                            .invalidateHttpSession(true)
                            .deleteCookies("SESSION", "JSESSION")
                            .logoutUrl("/api/v1/logout")
//                            .logoutSuccessUrl("/login.html")
                            .permitAll();
                });

        // Constructs the SecurityFilterChain
        return httpSecurity.build();
    }

    /**
     * Specifies to use our custom UserDetailsService class
     * @param http HttpSecurity the Security object to add our UserDetails into
     * @return AuthenticationManager modified AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(buzzUserDetailsManager).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}
