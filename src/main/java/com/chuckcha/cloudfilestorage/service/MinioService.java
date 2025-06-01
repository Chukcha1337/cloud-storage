package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.entity.Type;
import com.chuckcha.cloudfilestorage.exception.DataNotFoundException;
import com.chuckcha.cloudfilestorage.exception.MinioDeleteObjectException;
import com.chuckcha.cloudfilestorage.exception.MinioDownloadException;
import com.chuckcha.cloudfilestorage.exception.MinioUploadException;
import com.chuckcha.cloudfilestorage.util.PathDataHandler;
import com.chuckcha.cloudfilestorage.util.ZipStreamWriter;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.extractType;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    @Value("${minio.bucket-name}")
    private String bucket;

    private final MinioClient minioClient;

    public void upload(String objectName, MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception ex) {
            throw new MinioUploadException("Failed to upload file %s".formatted(file.getOriginalFilename()));
        }
    }

    public ResponseEntity<?> download(String objectName) throws IOException {
        return switch (extractType(objectName)) {
            case FILE -> downloadFile(objectName);
            case DIRECTORY -> downloadDirectoryAsZip(objectName);
        };
    }

    private InputStream downloadDirectoryAsZip(String dirName) {
        List<String> objectNames = listObjectNames(dirName);
        try (ZipStreamWriter zip = new ZipStreamWriter(outputStream)) {
            for (String name : objectNames) {
                String entryName = name.substring(dirName.length());
                try (InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket("my-bucketname")
                                .object("my-objectname")
                                .build())) {
                    zip.addFile(entryName, stream);
                }
            }
            return new ByteArrayInputStream(zip.);
        } catch (IOException e) {
            throw new MinioDownloadException("Failed to zip directory: %s".formatted(dirName), e);
        }
    }


    private InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new MinioDownloadException("Failed to download file %s".formatted(objectName), e);
        }
    }

    public void delete(String prefix) {
        List<DeleteObject> toDelete = listObjectNames(prefix)
                .stream()
                .map(DeleteObject::new)
                .toList();

        minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(bucket)
                        .objects(toDelete)
                        .build());
    }


    private List<String> listObjectNames(String prefix) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(prefix)
                            .recursive(true)
                            .build());

            List<String> names = new ArrayList<>();
            for (Result<Item> result : results) {
                names.add(result.get().objectName());
            }

            if (names.isEmpty()) {
                throw new DataNotFoundException("No data found to list");
            }
            return names;
        } catch (Exception e) {
            throw new MinioDownloadException("Failed to retrieve object names with prefix: " + prefix, e);
        }
    }
}

