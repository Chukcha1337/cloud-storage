package com.chuckcha.cloudfilestorage.controller;

import com.chuckcha.cloudfilestorage.dto.UsernameResponseDto;
import com.chuckcha.cloudfilestorage.security.model.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public UsernameResponseDto getUser(@AuthenticationPrincipal UserDetailsImpl user) {
        return new UsernameResponseDto(user.getUsername());
    }


}
