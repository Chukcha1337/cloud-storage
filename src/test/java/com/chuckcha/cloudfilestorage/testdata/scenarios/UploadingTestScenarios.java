package com.chuckcha.cloudfilestorage.testdata.scenarios;

import com.chuckcha.cloudfilestorage.testdata.data.TestFiles;
import com.chuckcha.cloudfilestorage.testdata.data.TestPaths;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

public class UploadingTestScenarios {

    public static Stream<Arguments> validUploadingScenarios() {
        return Stream.of(
                Arguments.of(TestPaths.FOLDER_NESTED.path(), TestFiles.mockMultipleValidFiles()),
                Arguments.of(TestPaths.FOLDER_NESTED.path(), new MultipartFile[]{TestFiles.NESTED_NAME_FILE})
        );
    }

    public static Stream<Arguments> invalidUploadingScenarios() {
        return Stream.of(
                Arguments.of(TestPaths.FOLDER_NESTED.path(), new MultipartFile[]{TestFiles.EMPTY_CONTENT_FILE}),
                Arguments.of(TestPaths.FILE_IN_FOLDER.path(), new MultipartFile[]{TestFiles.TEXT_FILE})
        );
    }

    public static Stream<Arguments> mixedUploadingScenarios() {
        return Stream.of(
                Arguments.of(TestPaths.FOLDER_NESTED.path(), TestFiles.mockFilesWithInvalids())
        );
    }

    public static Stream<Arguments> onlyOneValidUploadingFile() {
        return Stream.of(
                Arguments.of(TestPaths.FOLDER_NESTED.path(), new MultipartFile[]{TestFiles.TEXT_FILE})
        );
    }
}
