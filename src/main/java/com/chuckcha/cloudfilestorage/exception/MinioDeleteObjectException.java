package com.chuckcha.cloudfilestorage.exception;

public class MinioDeleteObjectException extends RuntimeException {

    public MinioDeleteObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioDeleteObjectException(String message) {
        super(message);
    }
}
