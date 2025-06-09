package com.chuckcha.cloudfilestorage.dto.request;

import com.chuckcha.cloudfilestorage.dto.request.path.PathRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SearchRequest(
        @NotBlank(message = "Path cannot be empty5")
        @Pattern(regexp = "^[a-zA-Z0-9._\\-/]+$", message = "Invalid search request format")
        String path) implements PathRequest {
}
