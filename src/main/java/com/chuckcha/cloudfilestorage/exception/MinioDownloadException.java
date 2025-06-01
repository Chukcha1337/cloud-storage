package com.chuckcha.cloudfilestorage.exception;

import io.minio.errors.MinioException;

public class MinioDownloadException extends RuntimeException {

    public MinioDownloadException(String formatted,Throwable e) {
        super(formatted, e);
    }

    public MinioDownloadException(String formatted) {
        super(formatted);
    }

}
