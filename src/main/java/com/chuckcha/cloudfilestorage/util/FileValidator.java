package com.chuckcha.cloudfilestorage.util;

import com.chuckcha.cloudfilestorage.dto.request.MetadataRequest;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.entity.Type;
import com.chuckcha.cloudfilestorage.exception.DataNotFoundException;
import com.chuckcha.cloudfilestorage.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.*;

@Component
@RequiredArgsConstructor
public class FileValidator {

    private final MetadataService metadataService;

    public void validateFiles(MultipartFile[] files, String fullPath) {

        if (files == null || files.length == 0) {
            throw new DataNotFoundException("No files to upload");
        }

        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            String fullFileName = fullPath + originalName;
            if (originalName == null || originalName.isBlank()) {
                throw new IllegalArgumentException("File name cannot be null or empty");
            }
            if (metadataService.exists(extractActualPath(fullFileName), extractName(fullFileName), Type.FILE)) {
                throw new DuplicateKeyException("There are duplicates at your files");
            }
        }
    }

    public void validateUpdatingFiles(MetadataRequest requestFrom, MetadataRequest requestTo) {
        if (!requestFrom.type().equals(requestTo.type())) {
            throw new RequestRejectedException("Request types mismatch");
        }
        if (requestFrom.fullPath().equals(requestTo.fullPath())) {
            throw new DuplicateKeyException("Cannot update metadata object with duplicate key");
        }
        if (!metadataService.exists(requestFrom)) {
            throw new DataNotFoundException("Cannot find source resource");
        }
        if (metadataService.exists(requestTo)) {
            throw new DuplicateKeyException("Updated resource already exists");
        }
        boolean anyMissing = extractFolders(requestTo.path()).entrySet().stream()
                .anyMatch((entry) -> !metadataService.exists(entry.getKey(), entry.getValue(), Type.DIRECTORY));
        if (anyMissing) {
            throw new RequestRejectedException("One or more folders in target path do not exist");
        }
        if (requestFrom.type().equals(Type.DIRECTORY) && requestTo.type().equals(Type.FILE)) {
            throw new IllegalArgumentException("Cannot rename or move directory to file");
        }
    }
}
