package com.chuckcha.cloudfilestorage.dto.request.path;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DirectoryPathDto(
        @Pattern(regexp = "^([a-zA-Z0-9-_]+/)*$", message = "Invalid path format")
        String path) implements PathRequest {

    @Override
    public String path() {
        return path == null ? "" : path;
    }

}
