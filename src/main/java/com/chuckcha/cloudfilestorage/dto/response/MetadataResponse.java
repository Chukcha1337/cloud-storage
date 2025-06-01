package com.chuckcha.cloudfilestorage.dto.response;

import com.chuckcha.cloudfilestorage.entity.Type;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MetadataResponse(String path, String name, Long size, Type type) {
}
