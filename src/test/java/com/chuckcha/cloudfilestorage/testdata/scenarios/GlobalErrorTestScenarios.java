package com.chuckcha.cloudfilestorage.testdata.scenarios;

import com.chuckcha.cloudfilestorage.dto.request.path.PathRequest;
import com.chuckcha.cloudfilestorage.testdata.data.TestPaths;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Function;
import java.util.stream.Stream;

public class GlobalErrorTestScenarios {

    public static Stream<Arguments> badRequestScenarios() {
        return Stream.of(
                prepareScenarios(TestPaths.invalidAnyPaths(),"GET", "/api/resource", "path"),
                prepareScenarios(TestPaths.invalidAnyPaths(),"DELETE","/api/resource", "path"),
                prepareScenarios(TestPaths.invalidAnyPaths(),"GET","/api/resource/download", "path"),
                prepareScenarios(TestPaths.invalidSearchRequests(),"GET","/api/resource/search", "query"),
                prepareScenarios(TestPaths.invalidDirectoryPaths(),"GET", "/api/directory", "path"),
                prepareScenarios(TestPaths.invalidDirectoryPaths(),"POST", "/api/directory", "path")
        ).flatMap(Function.identity());
    }

    public static Stream<Arguments> unauthorizedScenarios() {
        return Stream.of(
                prepareScenarios(Stream.of(TestPaths.FILE_IN_ROOT),"GET","/api/resource", "path"),
                prepareScenarios(Stream.of(TestPaths.FILE_IN_ROOT),"DELETE","/api/resource", "path"),
                prepareScenarios(Stream.of(TestPaths.FILE_IN_ROOT),"GET","/api/resource/download", "path"),
                prepareScenarios(Stream.of(TestPaths.FILE_IN_ROOT),"GET","/api/resource/search", "query"),
                prepareScenarios(Stream.of(TestPaths.FOLDER_NESTED),"GET", "/api/directory", "path"),
                prepareScenarios(Stream.of(TestPaths.FOLDER_NESTED),"POST", "/api/directory", "path")
        ).flatMap(Function.identity());
    }

    public static Stream<Arguments> notFoundScenarios() {
        return Stream.of(
                prepareScenarios(Stream.of(TestPaths.FILE_IN_ROOT),"GET","/api/resource", "path"),
                prepareScenarios(Stream.of(TestPaths.FILE_IN_ROOT),"DELETE","/api/resource", "path"),
                prepareScenarios(Stream.of(TestPaths.FILE_IN_ROOT),"GET","/api/resource/download", "path"),
                prepareScenarios(Stream.of(TestPaths.FOLDER_NESTED),"GET", "/api/directory", "path")
        ).flatMap(Function.identity());
    }

    private static Stream<Arguments> prepareScenarios(Stream<? extends PathRequest> values, String method, String endpoint, String param) {
        return values.map(dto -> Arguments.of(method, endpoint, param, dto.path()));
    }
}
