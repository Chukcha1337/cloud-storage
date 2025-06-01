package com.chuckcha.cloudfilestorage.exception;

import io.minio.errors.MinioException;

public class MinioDownloadException extends RuntimeException {
    public MinioDownloadException(String formatted,Throwable e) {
        super(formatted, e);
    }
}
