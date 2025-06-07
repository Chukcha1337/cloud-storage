package com.chuckcha.cloudfilestorage.config;

import com.chuckcha.cloudfilestorage.util.PathDataHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PathDataInitializer {

    @Value("${minio.root-user-directory}")
    private String rootUserDirectoryPattern;

    @PostConstruct
    public void init() {
        PathDataHandler.setRootUserDirectoryPattern(rootUserDirectoryPattern);
        log.info("PathDataHandler initialized with pattern: {}", rootUserDirectoryPattern);
    }
}
