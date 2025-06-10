package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.dto.request.path.MetadataRequest;
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

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.*;


@Service
@RequiredArgsConstructor
public class MetadataService {

    private final MetadataRepository metadataRepository;
    private final MetadataMapper metadataMapper;

    public MetadataResponse get(MetadataRequest request) {
        return metadataRepository.findByPathAndNameAndType(request.path(), request.name(), request.type())
                .map(metadataMapper::toResponse)
                .orElseThrow(() -> new DataNotFoundException("Failed to retrieve metadata for path: " + request.path()));
    }

    public MetadataResponse save(String path, MultipartFile file) {
        Metadata metadata = Metadata.builder()
                .path(extractActualPath(path))
                .name(extractName(path))
                .size(file.getSize())
                .type(Type.FILE)
                .build();

        Metadata saved = metadataRepository.save(metadata);
        return metadataMapper.toResponse(saved);
    }


    public Optional<MetadataResponse> createFolderIfNotExists(String folderPath, String folderName) {
        if (!exists(folderPath, folderName, Type.DIRECTORY)) {
            return Optional.ofNullable(
                    metadataMapper.toResponse(
                            metadataRepository
                                    .save(Metadata.builder()
                                            .path(folderPath)
                                            .name(folderName)
                                            .type(Type.DIRECTORY)
                                            .build())));
        }
        return Optional.empty();
    }

    public boolean exists(String path, String name, Type type) {
        return metadataRepository.existsByPathAndNameAndType(path, name, type);
    }

    public boolean exists(MetadataRequest request) {
        return metadataRepository.existsByPathAndNameAndType(request.path(), request.name(), request.type());
    }

    public void delete(MetadataRequest request) {
        if (exists(request)) {
            if (request.type().equals(Type.DIRECTORY)) {
                metadataRepository.deleteAllByPathStartingWith(request.path() + request.name() + "/");
            }
            metadataRepository.deleteByPathAndNameAndType(request.path(), request.name(), request.type());
        } else {
            throw new DataNotFoundException("Failed to delete metadata for path: " + request.path());
        }

    }

    public MetadataResponse updateFile(MetadataRequest requestFrom, MetadataRequest requestTo) {
        return metadataRepository.findByPathAndNameAndType(requestFrom.path(), requestFrom.name(), requestFrom.type())
                .map(from -> {
                    from.setName(requestTo.name());
                    from.setPath(requestTo.path());
                    return metadataRepository.save(from);
                })
                .map(metadataMapper::toResponse)
                .orElseThrow(() -> new DataNotFoundException("Failed to move metadata: %s".formatted(requestFrom.name())));
    }

    public MetadataResponse updateDir(MetadataRequest requestFrom, MetadataRequest requestTo) {
        String fromFullPath = requestFrom.fullPath();
        String toFullPath = requestTo.fullPath();

        List<Metadata> toMove = metadataRepository.findAllByPathStartsWith(fromFullPath);

        for (Metadata metadata : toMove) {
            String oldPath = metadata.getPath();
            String relative = oldPath.substring(fromFullPath.length());
            String newPath = toFullPath + relative;
            metadata.setPath(newPath);
        }

        metadataRepository.saveAll(toMove);

        Metadata rootDir = metadataRepository.findByPathAndNameAndType(
                        requestFrom.path(), requestFrom.name(), Type.DIRECTORY)
                .orElseThrow(() -> new DataNotFoundException("Root directory not found"));

        rootDir.setPath(requestTo.path());
        rootDir.setName(requestTo.name());
        return metadataMapper.toResponse(metadataRepository.save(rootDir));
    }

    public List<MetadataResponse> search(Long userId, String request) {
        String userDirectory = getUserDirectoryPath(userId);
        List<MetadataResponse> result = metadataRepository.search(userDirectory, request).stream().map(metadataMapper::toResponse).toList();
        if (result.isEmpty()) {
            throw new DataNotFoundException("Failed to search metadata: %s".formatted(request));
        }
        return result;
    }

    public List<MetadataResponse> getFolderContent(MetadataRequest request) {
        if (!exists(request)) {
            throw new DataNotFoundException("Failed find folder with path %s".formatted(request.fullPath()));
        }
        return metadataRepository.findAllByPath(request.fullPath()).stream().map(metadataMapper::toResponse).toList();
    }
}
