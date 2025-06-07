package com.chuckcha.cloudfilestorage.controller;


import com.chuckcha.cloudfilestorage.dto.request.DirectoryPathDto;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.security.model.UserDetailsImpl;
import com.chuckcha.cloudfilestorage.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/directory")
@RequiredArgsConstructor
@Validated
public class DirectoryController {

    private final ResourceService resourceService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MetadataResponse> getFolderContent(
            @Validated @ModelAttribute("path") DirectoryPathDto pathDto,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return resourceService.getFolderContent(user.getId(), pathDto.path());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<MetadataResponse> createFolder(
            @Validated @ModelAttribute("path") DirectoryPathDto pathDto,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return resourceService.createFolders(user.getId(), pathDto.path());
    }
}
