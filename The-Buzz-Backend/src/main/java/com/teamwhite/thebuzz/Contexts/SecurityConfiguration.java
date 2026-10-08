//package com.teamwhite.thebuzz.Contexts;
//
//import com.teamwhite.thebuzz.Services.BuzzUserDetailsManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.SecurityFilterChain;
//import javax.sql.DataSource;
//
//@Configuration
//@EnableWebSecurity
//class SecurityConfiguration {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
//        httpSecurity.authorizeHttpRequests((authorize)
//                -> authorize.anyRequest().permitAll())
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
//
//        return httpSecurity.build();
//    }
//
//    @Bean
//    BuzzUserDetailsManager users(DataSource dataSource) {
//        UserDetails user = User.builder()
//                .username("user")
//                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
//                .roles("USER", "ADMIN")
//                .build();
//        BuzzUserDetailsManager users = new BuzzUserDetailsManager();
//        users.createUser(user);
//        users.createUser(admin);
//        return users;
//    }
//}
