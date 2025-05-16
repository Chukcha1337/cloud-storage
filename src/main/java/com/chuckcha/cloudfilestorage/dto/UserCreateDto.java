package com.chuckcha.cloudfilestorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotBlank
    @Size(min = 3, max = 64)
    String username;

    @NotBlank
    @Size(min = 3, max = 64)
    String rawPassword;
}
