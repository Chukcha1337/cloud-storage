package com.chuckcha.cloudfilestorage.exception;

import io.minio.errors.MinioException;

import java.io.Serial;

public class MinioUploadException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MinioUploadException(String message) {
        super(message);
    }
}
