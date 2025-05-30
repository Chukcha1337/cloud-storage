package com.chuckcha.cloudfilestorage.util;

import com.chuckcha.cloudfilestorage.entity.Type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PathDataHandler {

    private PathDataHandler() {}

    public static String extractName(String path) {
        String trimmed = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        int lastSlash = trimmed.lastIndexOf('/');
        return trimmed.substring(lastSlash + 1);
    }

    public static String extractActualPath(String path) {
        String trimmed = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        int lastSlash = trimmed.lastIndexOf('/');
        if (lastSlash <= 0) return "";
        return path.substring(0, lastSlash + 1);
    }

    public static Type extractType(String path) {
        return path.endsWith("/") ? Type.DIRECTORY : Type.FILE;
    }

    public static Map<String, String> extractFolders(String path) {
        Map<String, String> folders = new HashMap<>();
        String[] parts = path.split("/");

        StringBuilder currentPath = new StringBuilder(parts[0]).append("/");
        int foldersIndex = path.endsWith("/") ? parts.length : parts.length -1;
        for (int i = 1; i < foldersIndex; i++) {
            folders.put(currentPath.toString(), parts[i]);
            currentPath.append(parts[i]).append("/");
        }
        return folders;
    }
}
