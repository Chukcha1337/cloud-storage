package com.chuckcha.cloudfilestorage.mapper;

import com.chuckcha.cloudfilestorage.dto.request.UserRegistrationRequest;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.dto.response.UserResponse;
import com.chuckcha.cloudfilestorage.entity.Metadata;
import com.chuckcha.cloudfilestorage.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MetadataMapper {

    Metadata toEntity(MetadataResponse metadataResponse);

    @Mapping(
            target = "size",
            expression = "java(metadata.getType() == Type.FILE ? metadata.getSize() : null)"
    )
    MetadataResponse toDto(Metadata metadata);
}
