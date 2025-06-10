package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.dto.request.path.MetadataRequest;
import com.chuckcha.cloudfilestorage.exception.*;
import com.chuckcha.cloudfilestorage.util.ZipStreamWriter;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


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


    public void writeDirectoryToStream(String dirName, OutputStream outputStream) throws IOException {
        List<String> objectNames = listObjectNames(dirName);
        try (ZipStreamWriter zip = new ZipStreamWriter(outputStream)) {
            for (String name : objectNames) {
                try (InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(name)
                                .build())) {
                    String entryName = name.substring(dirName.length());
                    zip.addFile(entryName, stream);
                }
            }
        } catch (Exception e) {
            throw new MinioDownloadException("Failed to zip directory: %s".formatted(dirName), e);
        }
    }

    public void writeFileToStream(String objectName, OutputStream outputStream) throws IOException {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build());) {
            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            throw new MinioDownloadException("Failed to download file %s".formatted(objectName), e);
        }
    }

    public void delete(String prefix) {
        List<DeleteObject> toDelete = listObjectNames(prefix)
                .stream()
                .map(DeleteObject::new)
                .toList();

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(bucket)
                        .objects(toDelete)
                        .build());

        for (Result<DeleteError> result : results) {
            try {
                DeleteError error = result.get();
                if (error != null) {
                    throw new MinioDeleteObjectException("Failed to delete object: %s, Message: %s".formatted(error.objectName(), error.message()));
                }
            } catch (Exception e) {
                throw new MinioDeleteObjectException("Exception during deletion of MinIO object", e);
            }
        }
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
            return names;
        } catch (Exception e) {
            throw new MinioDownloadException("Failed to retrieve object names with prefix: " + prefix, e);
        }
    }

    public void updateFile(MetadataRequest requestFrom, MetadataRequest requestTo) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(requestTo.fullPath())
                            .source(
                                    CopySource.builder()
                                            .bucket(bucket)
                                            .object(requestFrom.fullPath())
                                            .build())
                            .build());
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(requestFrom.fullPath())
                            .build());
        } catch (
                Exception e) {
            throw new MinioMoveException("Failed to move object: " + requestFrom.name(), e);
        }
    }


    public void updateDir(MetadataRequest requestFrom, MetadataRequest requestTo) {
        String currentDirPath = requestFrom.fullPath();
        String newDirPath = requestTo.fullPath();
        List<String> objectsToMove = listObjectNames(currentDirPath);
        List<String> copiedObjects = new ArrayList<>();
        try {
            for (String name : objectsToMove) {
                String newName = newDirPath.concat(name.substring(currentDirPath.length()));
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucket)
                                .object(newName)
                                .source(
                                        CopySource.builder()
                                                .bucket(bucket)
                                                .object(name)
                                                .build())
                                .build());
                copiedObjects.add(newName);
                log.info("Moving object: {} → {}", name, newName);
            }
            if (copiedObjects.size() == objectsToMove.size()) {
                delete(currentDirPath);
            } else {
                throw new MinioMoveException("Not all files copied. Aborting delete.");
            }
        } catch (Exception e) {
            throw new MinioMoveException("Failed to move object names with prefix: " + currentDirPath, e);
        }
    }
}



