package com.chuckcha.cloudfilestorage.config;

import com.chuckcha.cloudfilestorage.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.chuckcha.cloudfilestorage.security.filter.UnauthorizedLogoutFilter;
import com.chuckcha.cloudfilestorage.security.handler.JsonAuthenticationFailureHandler;
import com.chuckcha.cloudfilestorage.security.handler.JsonAuthenticationSuccessHandler;
import com.chuckcha.cloudfilestorage.util.JsonResponseHandler;
import com.chuckcha.cloudfilestorage.validation.RequestValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String LOGOUT_PATH = "/api/auth/sign-out";
    private static final String LOGIN_PATH = "/api/auth/sign-in";
    private static final String REGISTER_PATH = "/api/auth/sign-up";
    private static final String DOCS_PATH = "/v3/api-docs/**";
    private static final String SWAGGER_PATH = "/swagger-ui/**";

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler jsonAuthenticationSuccessHandler(
            SecurityContextRepository securityContextRepository,
            SecurityContextHolderStrategy securityContextHolderStrategy,
            JsonResponseHandler jsonResponseHandler) {
        return new JsonAuthenticationSuccessHandler(securityContextRepository, securityContextHolderStrategy, jsonResponseHandler);
    }

    @Bean
    public AuthenticationFailureHandler jsonAuthenticationFailureHandler(JsonResponseHandler jsonResponseHandler) {
        return new JsonAuthenticationFailureHandler(jsonResponseHandler);}

    @Bean
    public UnauthorizedLogoutFilter unauthorizedLogoutFilter(
            SecurityContextHolderStrategy securityContextHolderStrategy,
            JsonResponseHandler jsonResponseHandler) {
        return new UnauthorizedLogoutFilter(LOGOUT_PATH, jsonResponseHandler, securityContextHolderStrategy);
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                                                             AuthenticationSuccessHandler jsonAuthenticationSuccessHandler,
                                                                                             AuthenticationFailureHandler jsonAuthenticationFailureHandler,
                                                                                             JsonResponseHandler jsonResponseHandler,
                                                                                             RequestValidator requestValidator) throws Exception {
        var filter = new JsonUsernamePasswordAuthenticationFilter(jsonResponseHandler, requestValidator);
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(jsonAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(jsonAuthenticationFailureHandler);
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter,
                                                   UnauthorizedLogoutFilter unauthorizedLogoutFilter,
                                                   SecurityContextRepository securityContextRepository
                                                   ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(urlConfig -> urlConfig
                        .requestMatchers(REGISTER_PATH, LOGIN_PATH, DOCS_PATH, SWAGGER_PATH).permitAll()
                        .requestMatchers(LOGOUT_PATH).authenticated()
                        .anyRequest().authenticated())
                .addFilterAt(jsonUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(unauthorizedLogoutFilter, LogoutFilter.class)
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_PATH)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                        .deleteCookies("JSESSIONID"))
                .build();
    }
}
