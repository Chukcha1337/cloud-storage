package com.chuckcha.cloudfilestorage.util;

import com.chuckcha.cloudfilestorage.entity.Type;
import com.chuckcha.cloudfilestorage.exception.DataNotFoundException;
import com.chuckcha.cloudfilestorage.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.extractActualPath;
import static com.chuckcha.cloudfilestorage.util.PathDataHandler.extractName;

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
}
