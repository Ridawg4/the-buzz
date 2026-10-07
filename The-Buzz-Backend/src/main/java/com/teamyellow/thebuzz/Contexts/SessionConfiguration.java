//package com.teamyellow.thebuzz.Contexts;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
//import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableJdbcHttpSession
//public class SessionConfiguration {
//
//    @Bean
//    public DataSource dataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .addScript("org/springframework/session/jdbc/schema-h2.sql")
//                .build();
//    }
//}
