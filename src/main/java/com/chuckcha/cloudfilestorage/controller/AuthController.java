package com.chuckcha.cloudfilestorage.controller;

import com.chuckcha.cloudfilestorage.dto.request.UserLoginRequest;
import com.chuckcha.cloudfilestorage.dto.request.UserRegistrationRequest;
import com.chuckcha.cloudfilestorage.dto.response.UserResponse;
import com.chuckcha.cloudfilestorage.security.service.AuthenticationService;
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
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Validated @RequestBody UserRegistrationRequest userRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        UserResponse userResponse = userService.create(userRequest);
        authenticationService.authenticateAndLogin(userRequest.username(), userRequest.rawPassword(), request, response);
        return userResponse;
    }

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse authenticate(
            @Validated @RequestBody UserLoginRequest userRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        UserResponse userResponse = new UserResponse(userRequest.username());
        authenticationService.authenticateAndLogin(userRequest.username(), userRequest.password(), request, response);
        return userResponse;
    }
}