package com.chuckcha.cloudfilestorage.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityContextService {

    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;

    public void applyAuthenticationContext(Authentication authentication,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {

        System.out.println("🟢 APPLYING CONTEXT");
        System.out.println("🟢 Committed BEFORE session: " + response.isCommitted());

        // Создаём сессию вручную
        HttpSession session = request.getSession(true);
        System.out.println("🟢 Session ID: " + session.getId());

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        System.out.println("🟢 Saving SecurityContext: " + context);

        // Сохраняем контекст
        securityContextRepository.saveContext(context, request, response);

        System.out.println("🟢 Committed AFTER saveContext: " + response.isCommitted());
    }

    public boolean isAuthenticated() {
        Authentication authentication = this.securityContextHolderStrategy.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
