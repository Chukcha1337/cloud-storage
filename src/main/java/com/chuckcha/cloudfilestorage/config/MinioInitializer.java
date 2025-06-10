package com.chuckcha.cloudfilestorage.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioInitializer {

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing root Minio bucket {}", bucketName);
            var found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                log.info("Bucket {} already exists", bucketName);
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
    }
}
