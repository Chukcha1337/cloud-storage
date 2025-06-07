package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.dto.request.MetadataRequest;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.entity.Type;
import com.chuckcha.cloudfilestorage.exception.DataNotFoundException;
import com.chuckcha.cloudfilestorage.mapper.MetadataMapper;
import com.chuckcha.cloudfilestorage.util.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final MetadataService metadataService;
    private final MinioService minioService;
    private final FileValidator fileValidator;
    private final MetadataMapper metadataMapper;

    public MetadataResponse get(Long userId, String path) {
        log.info("User {} has started getting file at path {}", userId, path);
        MetadataRequest request = metadataMapper.toRequest(userId, path);
        return metadataService.get(request);
    }

    @Transactional
    public void delete(Long userId, String path) {
        log.info("User {} has started deleting file at path {}", userId, path);
        MetadataRequest request = metadataMapper.toRequest(userId, path);
        metadataService.delete(request);
        minioService.delete(request.fullPath());
        log.info("User {} has successfully finished deleting file at path {}", userId, path);
    }

    @Transactional
    public List<MetadataResponse> uploadFiles(Long userId, String path, MultipartFile[] files) {
        log.info("User {} has started uploading files to path {}", userId, path);
        List<MetadataResponse> responses = new ArrayList<>();
        String fullPath = getFullPath(userId, path);

        fileValidator.validateFiles(files, fullPath);

        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                throw new RequestRejectedException("File is empty");
            }
            String fullFileName = fullPath.concat(Objects.requireNonNull(file.getOriginalFilename()));
            createNewFolderIfNotExist(userId, fullFileName);

            minioService.upload(fullFileName, file);
            responses.add(metadataService.save(fullFileName, file));
        }
        if (!responses.isEmpty()) {
            log.info("User {} has finished uploading files to path {}: {}", userId, fullPath, responses.stream().map(MetadataResponse::toString).collect(Collectors.joining(",")));
        }
        return responses;
    }

    @Transactional
    public void writeDataToStream(Long userId, String path, OutputStream outputStream) throws IOException {
        log.info("User {} has started downloading {} at path", userId, path);
        MetadataRequest request = metadataMapper.toRequest(userId, path);
        switch (request.type()) {
            case FILE -> minioService.writeFileToStream(request.fullPath(), outputStream);
            case DIRECTORY -> minioService.writeDirectoryToStream(request.fullPath(), outputStream);
            default -> throw new IllegalStateException("Unknown type for object: " + request.fullPath());
        }
        log.info("User {} has finished downloading {} at path", userId, path);
    }

    @Transactional
    public MetadataResponse update(Long userId, String from, String to) {
        MetadataRequest requestFrom = metadataMapper.toRequest(userId, from);
        MetadataRequest requestTo = metadataMapper.toRequest(userId, to);
        fileValidator.validateUpdatingFiles(requestFrom, requestTo);
        switch (requestFrom.type()) {
            case Type.FILE -> {
                minioService.updateFile(requestFrom, requestTo);
                return metadataService.updateFile(requestFrom, requestTo);
            }
            case Type.DIRECTORY -> {
                minioService.updateDir(requestFrom, requestTo);
                return metadataService.updateDir(requestFrom, requestTo);
            }
            default -> throw new IllegalStateException("Unknown type for object: " + requestFrom.fullPath());
        }
    }

    @Transactional(readOnly = true)
    public List<MetadataResponse> search(Long userId, String request) {
        log.info("User {} has started searching {}", userId, request);
        return metadataService.search(userId, request);
    }

    @Transactional(readOnly = true)
    public List<MetadataResponse> getFolderContent(Long userId, String path) {
        MetadataRequest request = metadataMapper.toRequest(userId, path);
        return metadataService.getFolderContent(request);
    }

    @Transactional
    public List<MetadataResponse> createFolders(Long userId, String path) {
        MetadataRequest request = metadataMapper.toRequest(userId, path);
        List<MetadataResponse> response = createNewFolderIfNotExist(userId, request.fullPath());
        if (response.isEmpty()) {
            throw new DuplicateKeyException("Folder with path %s already exists".formatted(request.fullPath()));
        }
        return response;
    }

    private List<MetadataResponse> createNewFolderIfNotExist(Long userId, String fullPath) {
        List<MetadataResponse> savedFolders = extractFolders(fullPath).entrySet().stream()
                .map(entry ->
                        metadataService.createFolderIfNotExists((entry.getKey()), entry.getValue()))
                .flatMap(Optional::stream)
                .toList();
        if (!savedFolders.isEmpty()) {
            for (MetadataResponse savedFolder : savedFolders) {
                log.info("User {} has created new folder {} with path {}", userId, savedFolder.name(), savedFolder.path());
            }
        } else {
            log.info("There are no new folders for user {}", userId);
        }
        return savedFolders;
    }

    public void checkIfExists(Long userId, String path) {
        MetadataRequest request = metadataMapper.toRequest(userId, path);
        if (!metadataService.exists(request)) {
            throw new DataNotFoundException("Data with path %s does not exist".formatted(request.fullPath()));
        }
    }
}

