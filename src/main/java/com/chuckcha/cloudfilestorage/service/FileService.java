package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.dto.response.DuplicateResponse;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.dto.response.Response;
import com.chuckcha.cloudfilestorage.entity.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.*;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${minio.bucket-name}")
    private String bucket;

    @Value("${minio.root-user-directory}")
    private String rootUserDirectoryPattern;

    private final MetadataService metadataService;
    private final MinioService minioService;

    public MetadataResponse get(Long userId, String path) {
        String fullPath = rootUserDirectoryPattern.formatted(userId) + path;
        return metadataService.get(
                extractActualPath(fullPath),
                extractName(fullPath),
                extractType(fullPath)
        );
    }


    @Transactional
    public void delete(Long userId, String path) {
        String fullPath = rootUserDirectoryPattern.formatted(userId) + path;
            metadataService.delete(extractActualPath(fullPath),
                    extractName(fullPath),
                    extractType(fullPath));
        minioService.delete(fullPath);

    }

    @Transactional
    public List<Response> uploadFiles(Long userId, String path, MultipartFile[] files) {

        List<Response> responses = new ArrayList<>();
        String fullPath = rootUserDirectoryPattern.formatted(userId) + path;

        Map<String, String> pathFolders = extractFolders(fullPath);
        pathFolders.forEach(metadataService::createFolderIfNotExists);

        for (MultipartFile file : files) {
            String pathAndName = file.getOriginalFilename();
            if (pathAndName == null || pathAndName.isBlank()) {
                throw new IllegalArgumentException("File name cannot be null or empty");
            }

            Map<String, String> filenameFolders = extractFolders(pathAndName);
            filenameFolders.forEach((folderPath, folderName) ->
                    metadataService.createFolderIfNotExists(fullPath + folderPath, folderName));

            String fullFileName = fullPath + pathAndName;

            if (metadataService.exists(extractActualPath(fullFileName), extractName(fullFileName), Type.FILE)) {
                responses.add(DuplicateResponse.builder()
                        .path(extractActualPath(fullFileName))
                        .name(extractName(fullFileName))
                        .build());
                continue;
            }

            minioService.upload(bucket, fullFileName, file);
            responses.add(metadataService.save(fullFileName, file));
        }
        return responses;
    }
}

