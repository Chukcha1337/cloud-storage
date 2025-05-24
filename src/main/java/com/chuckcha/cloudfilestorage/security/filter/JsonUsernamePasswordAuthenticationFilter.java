package com.chuckcha.cloudfilestorage.security.filter;

import com.chuckcha.cloudfilestorage.dto.request.UserLoginRequest;
import com.chuckcha.cloudfilestorage.util.JsonResponseHandler;
import com.chuckcha.cloudfilestorage.validation.RequestValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JsonResponseHandler jsonResponseHandler;
    private final RequestValidator requestValidator;

    public JsonUsernamePasswordAuthenticationFilter(JsonResponseHandler jsonResponseHandler, RequestValidator requestValidator) {
        super(new AntPathRequestMatcher("/api/auth/sign-in", "POST"));
        this.jsonResponseHandler = jsonResponseHandler;
        this.requestValidator = requestValidator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (request.getContentType() == null || !request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            throw new AuthenticationServiceException("Only application/json content type is supported");
        }
        UserLoginRequest loginRequest = jsonResponseHandler.readValue(request.getInputStream(), UserLoginRequest.class);

        try {
            requestValidator.validate(loginRequest);
        } catch (ConstraintViolationException ex) {
            throw new AuthenticationServiceException("Validation failed: " + ex.getMessage(), ex);
        }

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                );

        return getAuthenticationManager().authenticate(authRequest);
    }
}
