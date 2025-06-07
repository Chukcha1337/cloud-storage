package com.chuckcha.cloudfilestorage.util;

import com.chuckcha.cloudfilestorage.exception.MinioDownloadException;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipStreamWriter implements AutoCloseable {

    private final ZipOutputStream zipOut;

    public ZipStreamWriter(OutputStream outputStream) {
        zipOut = new ZipOutputStream(outputStream);
    }

    public void addFile(String entryName, InputStream inputStream) {
        try {
            zipOut.putNextEntry(new ZipEntry(entryName));
            inputStream.transferTo(zipOut);
            zipOut.closeEntry();
        } catch (Exception e) {
            throw new MinioDownloadException("Failed to add entry to zip: %s".formatted(entryName), e);
        }
    }

    @Override
    public void close() throws Exception {
        zipOut.finish();
        zipOut.close();
    }
}

