package com.chuckcha.cloudfilestorage.config;

import com.chuckcha.cloudfilestorage.dto.response.ErrorResponse;
import com.chuckcha.cloudfilestorage.security.AuthenticationEntryPointImpl;
import com.chuckcha.cloudfilestorage.security.filter.UnauthorizedLogoutFilter;
import com.chuckcha.cloudfilestorage.security.service.SecurityContextService;
import com.chuckcha.cloudfilestorage.util.JsonResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
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
    public UnauthorizedLogoutFilter unauthorizedLogoutFilter(
            JsonResponseHandler jsonResponseHandler,
            SecurityContextService securityContextService) {
        return new UnauthorizedLogoutFilter(LOGOUT_PATH, jsonResponseHandler, securityContextService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UnauthorizedLogoutFilter unauthorizedLogoutFilter,
                                                   SecurityContextRepository securityContextRepository,
                                                   AuthenticationEntryPointImpl authenticationEntryPoint) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeHttpRequests(urlConfig -> urlConfig
                        .requestMatchers(REGISTER_PATH, LOGIN_PATH, DOCS_PATH, SWAGGER_PATH).permitAll()
                        .requestMatchers(LOGOUT_PATH).authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(unauthorizedLogoutFilter, LogoutFilter.class)
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_PATH)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                        .deleteCookies("SESSION"))
                .build();
    }
}
