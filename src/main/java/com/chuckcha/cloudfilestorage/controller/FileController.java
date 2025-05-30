package com.chuckcha.cloudfilestorage.controller;

import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.dto.response.Response;
import com.chuckcha.cloudfilestorage.exception.DataNotFoundException;
import com.chuckcha.cloudfilestorage.exception.InvalidPathException;
import com.chuckcha.cloudfilestorage.security.model.UserDetailsImpl;
import com.chuckcha.cloudfilestorage.service.FileService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/resource/")
@RequiredArgsConstructor
@Validated
public class FileController {

    private final FileService fileService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MetadataResponse getMetadata(
            @RequestParam
            @NotBlank(message = "Path cannot be empty")
            @Pattern(
                    regexp = "^([a-zA-Z0-9._-]+/)*[a-zA-Z0-9._-]+/?$",
                    message = "Invalid path format"
            )
            String path,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return fileService.get(user.getId(), path);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<Response> uploadFiles(
            @RequestParam
            @NotBlank(message = "Path cannot be empty")
            @Pattern(
                    regexp = "^([a-zA-Z0-9-_]+/)*$",
                    message = "Invalid path format"
            )
            String path,
            @RequestParam("files") MultipartFile[] files,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        if (files == null || files.length == 0) {
            throw new DataNotFoundException("No files to upload");
        }
        return fileService.uploadFiles(user.getId(), path, files);
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestParam
            @NotBlank(message = "Path cannot be empty")
            @Pattern(
                    regexp = "^([a-zA-Z0-9-_]+/)*$",
                    message = "Invalid path format"
            )
            String path,
            @AuthenticationPrincipal UserDetailsImpl user) {
        fileService.delete(user.getId(), path);
    }


}
