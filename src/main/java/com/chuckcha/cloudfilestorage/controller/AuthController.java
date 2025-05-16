package com.chuckcha.cloudfilestorage.controller;

import com.chuckcha.cloudfilestorage.dto.UserCreateDto;
import com.chuckcha.cloudfilestorage.dto.UsernameResponseDto;
import com.chuckcha.cloudfilestorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping(value ="/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UsernameResponseDto create(@Validated @RequestBody UserCreateDto user) {
        return userService.create(user);
    }

}
