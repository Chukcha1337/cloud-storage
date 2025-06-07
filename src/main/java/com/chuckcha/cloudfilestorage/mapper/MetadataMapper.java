package com.chuckcha.cloudfilestorage.mapper;

import com.chuckcha.cloudfilestorage.dto.request.MetadataRequest;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.entity.Metadata;
import com.chuckcha.cloudfilestorage.util.PathDataHandler;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.*;

@Mapper(componentModel = "spring")
public interface MetadataMapper {

    Metadata toEntity(MetadataResponse metadataResponse);

    @Mapping(
            target = "size",
            expression = "java(metadata.getType() == Type.FILE ? metadata.getSize() : null)"
    )
    MetadataResponse toResponse(Metadata metadata);

    default MetadataRequest toRequest(Long userId, String path) {
        String fullPath = getFullPath(userId, path);
        return new MetadataRequest(
                userId,
                fullPath,
                extractActualPath(fullPath),
                extractName(fullPath),
                extractType(fullPath));
    }
}
