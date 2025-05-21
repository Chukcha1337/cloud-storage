package com.chuckcha.cloudfilestorage.config;

import com.chuckcha.cloudfilestorage.security.entryPoint.JsonAuthenticationEntryPoint;
import com.chuckcha.cloudfilestorage.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.chuckcha.cloudfilestorage.security.handler.JsonAuthenticationFailureHandler;
import com.chuckcha.cloudfilestorage.security.handler.JsonAuthenticationSuccessHandler;
import com.chuckcha.cloudfilestorage.security.handler.JsonLogoutSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String LOGOUT_PATH = "/api/auth/sign-out";
    private static final String LOGIN_PATH = "/api/auth/sign-out";
    private static final String REGISTER_PATH = "/api/auth/sign-out";

    private final ObjectMapper objectMapper;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy() {
        return SecurityContextHolder.getContextHolderStrategy();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler jsonAuthenticationSuccessHandler(
            SecurityContextRepository securityContextRepository,
            SecurityContextHolderStrategy securityContextHolderStrategy) {
        return new JsonAuthenticationSuccessHandler(objectMapper, securityContextRepository, securityContextHolderStrategy);
    }

    @Bean
    public AuthenticationFailureHandler jsonAuthenticationFailureHandler() {
        return new JsonAuthenticationFailureHandler(objectMapper);}

    @Bean
    public LogoutSuccessHandler jsonLogoutSuccessHandler(SecurityContextHolderStrategy securityContextHolderStrategy) {
        return new JsonLogoutSuccessHandler(objectMapper, securityContextHolderStrategy);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter,
                                                   LogoutSuccessHandler jsonLogoutSuccessHandler
                                                   ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(urlConfig -> urlConfig
                        .requestMatchers("/api/auth/sign-up", "/api/auth/sign-in", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/api/auth/sign-out").authenticated()
                        .anyRequest().authenticated())
                .addFilterAt(jsonUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/auth/sign-out")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint))
                .build();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                                                             AuthenticationSuccessHandler jsonAuthenticationSuccessHandler,
                                                                                             AuthenticationFailureHandler jsonAuthenticationFailureHandler) throws Exception {
        var filter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(jsonAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(jsonAuthenticationFailureHandler);
        return filter;
    }
}
