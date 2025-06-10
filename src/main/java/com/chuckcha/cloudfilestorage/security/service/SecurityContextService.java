package com.chuckcha.cloudfilestorage.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityContextService {

    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;

    public void applyAuthenticationContext(Authentication authentication,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {

        log.info("Apply authentication context");
        log.info("Committed BEFORE session: {}", response.isCommitted());
        HttpSession session = request.getSession(true);
        log.info("Session ID: {}", session.getId());

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);

        log.info("Saving SecurityContext: {}", context);
        securityContextRepository.saveContext(context, request, response);
    }

    public boolean isAuthenticated() {
        Authentication authentication = this.securityContextHolderStrategy.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
