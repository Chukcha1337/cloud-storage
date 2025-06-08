package com.chuckcha.cloudfilestorage.dto.request.path;

import com.chuckcha.cloudfilestorage.entity.Type;

public record MetadataRequest(Long userId, String fullPath, String path, String name, Type type) {
}
