package com.chuckcha.cloudfilestorage.util;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

public class TestUpdatingScenarios {

    public static Stream<Arguments> updatingValidScenarios() {
        return Stream.of(
                // FILE -> FILE only new path
                Arguments.of(
                        TestPaths.FOLDER_NESTED.path().concat(TestFiles.TEXT_FILE.getOriginalFilename()),
                        TestPaths.FOLDER_SIMPLE.path().concat(TestFiles.TEXT_FILE.getOriginalFilename()),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // DIR -> DIR only new path
                Arguments.of(
                        TestPaths.FOLDER_NESTED.path(),
                        TestPaths.FOLDER_WITH_MOVED_SUBFOLDER.path(),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // FILE -> FILE only new name
                Arguments.of(
                        TestPaths.FOLDER_NESTED.path().concat(TestFiles.TEXT_FILE.getOriginalFilename()),
                        TestPaths.FOLDER_NESTED.path().concat("new_filename.txt"),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // DIR -> DIR only new name
                Arguments.of(
                        TestPaths.FOLDER_NESTED.path(),
                        TestPaths.FOLDER_WITH_RENAMED_SUBFOLDER.path(),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // FILE -> FILE new path and name
                Arguments.of(
                        TestPaths.FOLDER_NESTED.path().concat(TestFiles.TEXT_FILE.getOriginalFilename()),
                        TestPaths.FOLDER_SIMPLE.path().concat("new_filename.txt"),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // DIR -> DIR new path and name
                Arguments.of(
                        TestPaths.FOLDER_NESTED.path(),
                        TestPaths.FOLDER_WITH_RENAMED_AND_MOVED_SUBFOLDER.path(),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                )
        );
    }

    public static Stream<Arguments> updatingInvalidPathsScenarios() {
        return Stream.of(
                // Invalid FROM
                Arguments.of(
                        TestPaths.ONLY_SLASHES.path(),
                        TestPaths.FOLDER_WITH_DASH.path(),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // Invalid TO
                Arguments.of(
                        TestPaths.FOLDER_WITH_DASH.path(),
                        TestPaths.ONLY_SLASHES.path(),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // Empty FROM and TO
                Arguments.of(
                        null,
                        null,
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // from DIR to FILE
                Arguments.of(
                        TestPaths.FOLDER_NESTED.path(),
                        TestPaths.FOLDER_SIMPLE.path().concat("filename.txt"),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ),
                // from FILE to DIR
                Arguments.of(
                        TestPaths.FOLDER_SIMPLE.path().concat("filename.txt"),
                        TestPaths.FOLDER_NESTED.path(),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ));
    }

    public static Stream<Arguments> oneUpdatingValidScenario() {
        return Stream.of(
                Arguments.of(
                        TestPaths.FOLDER_NESTED.path().concat(TestFiles.TEXT_FILE.getOriginalFilename()),
                        TestPaths.FOLDER_SIMPLE.path().concat(TestFiles.TEXT_FILE.getOriginalFilename()),
                        new MultipartFile[]{TestFiles.TEXT_FILE}
                ));
    }

}
