package com.chuckcha.cloudfilestorage.controller;

import com.chuckcha.cloudfilestorage.dto.request.SearchRequest;
import com.chuckcha.cloudfilestorage.dto.request.path.AnyPathDto;
import com.chuckcha.cloudfilestorage.dto.request.path.DirectoryPathDto;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.security.model.UserDetailsImpl;
import com.chuckcha.cloudfilestorage.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.extractName;

@RestController
@RequestMapping("api/resource")
@RequiredArgsConstructor
@Validated
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MetadataResponse getMetadata(
            @Validated @ModelAttribute("path") AnyPathDto pathDto,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return resourceService.get(user.getId(), pathDto.path());
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download(
            @Validated @ModelAttribute("path") AnyPathDto pathDto,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        resourceService.checkIfExists(user.getId(), pathDto.path());
        StreamingResponseBody body = outputStream -> {
            resourceService.writeDataToStream(user.getId(), pathDto.path(), outputStream);
        };
        var filename = pathDto.path().endsWith("/") ? "new_folder.zip" : extractName(pathDto.path());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(filename))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(body);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<MetadataResponse> search(
            @Validated @ModelAttribute("query") SearchRequest query,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return resourceService.search(user.getId(), query.path());
    }

    @GetMapping("/move")
    @ResponseStatus(HttpStatus.OK)
    public MetadataResponse update(
            @Validated @ModelAttribute("from") AnyPathDto from,
            @Validated @ModelAttribute("to") AnyPathDto to,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return resourceService.update(user.getId(), from.path(), to.path());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<MetadataResponse> uploadFiles(
            @Validated @ModelAttribute("path") DirectoryPathDto pathDto,
            @RequestParam MultipartFile[] files,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        return resourceService.uploadFiles(user.getId(), pathDto.path(), files);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Validated @ModelAttribute("path") AnyPathDto pathDto,
            @AuthenticationPrincipal UserDetailsImpl user) {
        resourceService.delete(user.getId(), pathDto.path());
    }
}
