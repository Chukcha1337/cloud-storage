package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.entity.Type;
import com.chuckcha.cloudfilestorage.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.*;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${minio.root-user-directory}")
    private String rootUserDirectoryPattern;

    private final MetadataService metadataService;
    private final MinioService minioService;
    private final FileValidator fileValidator;

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
    public List<MetadataResponse> uploadFiles(Long userId, String path, MultipartFile[] files) {

        List<MetadataResponse> responses = new ArrayList<>();
        String fullPath = rootUserDirectoryPattern.formatted(userId) + path;

        fileValidator.validateFiles(files, fullPath);

        Map<String, String> pathFolders = extractFolders(fullPath);
        pathFolders.forEach(metadataService::createFolderIfNotExists);

        for (MultipartFile file : files) {
            String pathAndName = file.getOriginalFilename();
            String fullFileName = fullPath + pathAndName;

            Map<String, String> fileNameFolders = extractFolders(pathAndName);
            fileNameFolders.forEach((folderPath, folderName) ->
                    metadataService.createFolderIfNotExists(fullPath + folderPath, folderName));

            minioService.upload(fullFileName, file);
            responses.add(metadataService.save(fullFileName, file));
        }
        return responses;
    }
}

