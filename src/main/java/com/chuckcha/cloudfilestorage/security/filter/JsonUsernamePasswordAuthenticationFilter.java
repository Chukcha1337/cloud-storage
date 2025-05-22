package com.chuckcha.cloudfilestorage.security.filter;

import com.chuckcha.cloudfilestorage.dto.UserCreateDto;
import com.chuckcha.cloudfilestorage.dto.UserLoginDto;
import com.chuckcha.cloudfilestorage.util.JsonResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    public JsonUsernamePasswordAuthenticationFilter(JsonResponseHandler jsonResponseHandler) {
        super(new AntPathRequestMatcher("/api/auth/sign-in", "POST"));
        this.jsonResponseHandler = jsonResponseHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (request.getContentType() == null || !request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            throw new AuthenticationServiceException("Only application/json content type is supported");
        }
        UserLoginDto userLoginDto = jsonResponseHandler.readValue(request.getInputStream(), UserLoginDto.class);

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(
                        userLoginDto.username(),
                        userLoginDto.password()
                );

        return getAuthenticationManager().authenticate(authRequest);
    }
}
