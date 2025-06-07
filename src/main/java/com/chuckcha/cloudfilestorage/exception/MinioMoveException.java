package com.chuckcha.cloudfilestorage.exception;

public class MinioMoveException extends RuntimeException {

    public MinioMoveException(String formatted,Throwable e) {
        super(formatted, e);
    }

    public MinioMoveException(String formatted) {
        super(formatted);
    }
}
