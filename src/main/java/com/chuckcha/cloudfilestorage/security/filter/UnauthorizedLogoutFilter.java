package com.chuckcha.cloudfilestorage.security.filter;

import com.chuckcha.cloudfilestorage.util.JsonResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class UnauthorizedLogoutFilter extends OncePerRequestFilter {

    private final String logoutPath;
    private final JsonResponseHandler jsonResponseHandler;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


log.info(securityContextHolderStrategy.toString());
        Authentication auth = securityContextHolderStrategy.getContext().getAuthentication();

        boolean isLogoutRequest = request.getMethod().equalsIgnoreCase("POST")
                                  && request.getRequestURI().equals(logoutPath);
        boolean isAnonymous = auth == null
                              || !auth.isAuthenticated()
                              || auth instanceof AnonymousAuthenticationToken;

        if (isLogoutRequest && isAnonymous) {
            jsonResponseHandler.writeJsonResponse(response, HttpStatus.UNAUTHORIZED, "User must be authorized to logout");
            return;
        }
        filterChain.doFilter(request, response);
    }
}

