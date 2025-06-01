package com.chuckcha.cloudfilestorage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DuplicateResponse {
    private String path;
    private String name;

    @Builder.Default
    private String status = "Current file cannot be uploaded because database already has such file";
}
