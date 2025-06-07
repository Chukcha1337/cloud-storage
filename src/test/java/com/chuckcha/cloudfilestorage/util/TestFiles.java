package com.chuckcha.cloudfilestorage.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

public class TestFiles {

    public static final MultipartFile TEXT_FILE = new MockMultipartFile(
            "text.txt",
            "text.txt",
            "text/plain",
            "Hello, World!".getBytes(StandardCharsets.UTF_8)
    );

    public static final MultipartFile PDF_FILE = new MockMultipartFile(
            "plan.pdf",
            "plan.pdf",
            "application/pdf",
            new byte[]{1, 2, 3, 4}
    );

    public static final MultipartFile NESTED_NAME_FILE = new MockMultipartFile(
            "folder/file.txt",
            "folder/file.txt",
            "text/plain",
            "Nested file".getBytes(StandardCharsets.UTF_8)
    );

    public static final MultipartFile EMPTY_CONTENT_FILE = new MockMultipartFile(
            "empty.txt",
            "empty.txt",
            "text/plain",
            new byte[0]
    );

    public static final MultipartFile OVERSIZED_FILE = new MockMultipartFile(
            "big.dat",
            "big.dat",
            "application/octet-stream",
            new byte[1024 * 1024 * 101]  // 101 MB
    );

    public static MultipartFile[] mockMultipleValidFiles() {
        return new MultipartFile[]{TEXT_FILE, PDF_FILE, NESTED_NAME_FILE};
    }

    public static MultipartFile[] mockFilesWithInvalids() {
        return new MultipartFile[]{
                TEXT_FILE,
                NESTED_NAME_FILE,
                EMPTY_CONTENT_FILE
        };
    }

    public static MultipartFile[] mockOnlyInvalidFiles() {
        return new MultipartFile[]{
                EMPTY_CONTENT_FILE
        };
    }
}

