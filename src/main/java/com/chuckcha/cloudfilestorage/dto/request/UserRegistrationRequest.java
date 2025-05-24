package com.chuckcha.cloudfilestorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

        @NotBlank(message = "Cannot be empty")
        @Size(min = 5, max = 20, message = "Must be {min}-{max} chars")
        @Pattern(
                regexp = "^[a-zA-Z0-9]+\\w*[a-zA-Z0-9]+$",
                message = "Invalid format"
        )
        String username,

        @NotBlank(message = "Cannot be empty")
        @Size(min = 5, max = 20, message = "Must be {min}-{max} chars")
        @Pattern(
                regexp = "^[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>\\[\\]/`~+=\\-_';]*$",
                message = "Invalid characters"
        )
        String rawPassword) {
}
