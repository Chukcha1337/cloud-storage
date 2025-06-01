package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.entity.Type;
import com.chuckcha.cloudfilestorage.exception.MinioDeleteObjectException;
import com.chuckcha.cloudfilestorage.exception.MinioDownloadException;
import com.chuckcha.cloudfilestorage.exception.MinioUploadException;
import com.chuckcha.cloudfilestorage.util.PathDataHandler;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.extractType;


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

    public InputStream download(String objectName) {
        if (extractType(objectName).equals(Type.FILE)) {
            try  {
                InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .build());
               return stream;
            }
            catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
                throw new MinioDownloadException("Failed to download file %s".formatted(objectName), e);
            }
        } else if (extractType(objectName).equals(Type.DIRECTORY)) {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(objectName)
                            .recursive(true)
                            .build());

            List<String> objectsToDownload = new ArrayList<>();

            for (Result<Item> result : results) {
                try {
                    objectsToDownload.add(result.get().objectName());
                } catch (Exception e) {
                    throw new MinioDeleteObjectException("Failed to find objects to remove", e);
                }
            }







        }


    }

    public void delete(String prefix) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucket)
                        .prefix(prefix)
                        .recursive(true)
                        .build());

        List<DeleteObject> objectsToDelete = new ArrayList<>();

        for (Result<Item> result : results) {
            try {
                objectsToDelete.add(new DeleteObject(result.get().objectName()));
            } catch (Exception e) {
                throw new MinioDeleteObjectException("Failed to find objects to remove", e);
            }
        }

        if (!objectsToDelete.isEmpty()) {
            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucket)
                            .objects(objectsToDelete)
                            .build());
        }
    }
}
