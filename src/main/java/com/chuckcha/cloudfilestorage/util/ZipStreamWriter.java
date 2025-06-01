package com.chuckcha.cloudfilestorage.util;

import com.chuckcha.cloudfilestorage.exception.MinioDownloadException;
import io.minio.GetObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipStreamWriter implements AutoCloseable {

    private final ZipOutputStream zipOut;

    public ZipStreamWriter(OutputStream outputStream) {
        zipOut = new ZipOutputStream(outputStream);
    }

    public void addFile(String entryName, InputStream content) {
        try {
            zipOut.putNextEntry(new ZipEntry(entryName));
            content.transferTo(zipOut);
            zipOut.closeEntry();
        } catch (Exception e) {
            throw new MinioDownloadException("Failed to get object: %s".formatted(entryName), e);
        }
    }

    @Override
    public void close() throws Exception {
        zipOut.finish();
        zipOut.close();
    }
}

