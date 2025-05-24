package com.chuckcha.cloudfilestorage.controller;

import com.chuckcha.cloudfilestorage.dto.request.UserRegistrationRequest;
import com.chuckcha.cloudfilestorage.dto.response.UserResponse;
import com.chuckcha.cloudfilestorage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    @PostMapping(value ="/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> register(
            @Validated @RequestBody UserRegistrationRequest userDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
            UserResponse dto = userService.create(userDto);

        var authRequest = new UsernamePasswordAuthenticationToken(
                userDto.username(),
                userDto.rawPassword()
        );
        Authentication authentication = authenticationManager.authenticate(authRequest);

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping(value ="/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> authenticate(
            @Validated @RequestBody UserRegistrationRequest userDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        var authRequest = new UsernamePasswordAuthenticationToken(
                userDto.username(),
                userDto.rawPassword()
        );
        Authentication authentication = authenticationManager.authenticate(authRequest);

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}