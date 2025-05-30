package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.entity.Metadata;
import com.chuckcha.cloudfilestorage.entity.Type;
import com.chuckcha.cloudfilestorage.exception.DataNotFoundException;
import com.chuckcha.cloudfilestorage.mapper.MetadataMapper;
import com.chuckcha.cloudfilestorage.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.extractActualPath;
import static com.chuckcha.cloudfilestorage.util.PathDataHandler.extractName;

@Service
@RequiredArgsConstructor
public class MetadataService {

    private final MetadataRepository metadataRepository;
    private final MetadataMapper metadataMapper;

    public MetadataResponse get(String path, String name, Type type) {
        return metadataRepository.findByPathAndNameAndType(path, name, type)
                .map(metadataMapper::toDto)
                .orElseThrow(() -> new DataNotFoundException("Failed to retrieve metadata for path: " + path));
    }

    public MetadataResponse save(Metadata metadata) {
        return metadataRepository.save(metadata)
                .map(metadataMapper::toDto).orElseThrow(() -> new DataNotFoundException("Failed to save metadata: %s".formatted(metadata.getName())));
    }

    public MetadataResponse save(String fullName, MultipartFile file) {
        Metadata metadata = Metadata.builder()
                .path(extractActualPath(fullName))
                .name(extractName(fullName))
                .size(file.getSize())
                .type(Type.FILE)
                .build();

        return metadataRepository.save(metadata)
                .map(metadataMapper::toDto).orElseThrow(() -> new DataNotFoundException("Failed to save metadata: %s".formatted(metadata.getName())));
    }


    public void createFolderIfNotExists(String folderPath, String folderName) {
        if (!exists(folderPath, folderName, Type.DIRECTORY)) {
            save(Metadata.builder()
                    .path(folderPath)
                    .name(folderName)
                    .type(Type.DIRECTORY)
                    .build());
        }
    }

    public boolean exists(String path, String name, Type type) {
        return metadataRepository.existsByPathAndNameAndType(path, name, type);
    }

    public void delete(String path, String name, Type type) {
        if (type.equals(Type.DIRECTORY)) {
            metadataRepository.deleteAllByPathStartingWith(path + name + "/");
        }
        metadataRepository.deleteByPathAndNameAndType(path, name, type);

    }
}
