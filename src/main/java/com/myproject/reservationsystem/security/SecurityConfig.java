package com.myproject.reservationsystem.security;

import com.myproject.reservationsystem.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); // set the custom user details service
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET, "/").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/user").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/user").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/user/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/user/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN"));

        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
