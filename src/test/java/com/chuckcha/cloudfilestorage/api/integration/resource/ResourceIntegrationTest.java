package com.chuckcha.cloudfilestorage.api.integration.resource;

import com.chuckcha.cloudfilestorage.api.integration.AbstractIntegrationTest;
import com.chuckcha.cloudfilestorage.dto.request.path.AnyPathDto;
import com.chuckcha.cloudfilestorage.dto.request.path.MetadataRequest;
import com.chuckcha.cloudfilestorage.dto.response.ErrorResponse;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.testdata.data.TestFiles;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.chuckcha.cloudfilestorage.util.PathDataHandler.extractActualPath;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class ResourceIntegrationTest extends AbstractIntegrationTest {

    @DisplayName("Successful getting info about valid resource test")
    @ParameterizedTest(name = "Getting info about resource with path {0}")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.data.TestPaths#validFileAndDirectoryPaths")
    public void shouldGetInfoAboutResource(AnyPathDto anyPathDto) {

        Map<String, String> cookies = authUserWithCookies();

        Long userId = userRepository.findByUsername(validUserToTest.username()).orElseThrow().getId();
        MetadataRequest request = metadataMapper.toRequest(userId, anyPathDto.path());

        switch (request.type()) {
            case FILE:
                metadataService.save(request.fullPath(), TestFiles.TEXT_FILE);
            case DIRECTORY:
                metadataService.createFolderIfNotExists(request.path(), request.name());
        }

        MetadataResponse response = given()
                .cookies(cookies)
                .queryParam("path", anyPathDto.path())
                .when()
                .get("api/resource")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(MetadataResponse.class);

        assertThat(response.path()).isEqualTo(request.path());
        assertThat(response.name()).isEqualTo(request.name());
        assertThat(response.type()).isEqualTo(request.type());
    }

    @DisplayName("Successful deleting resource test")
    @ParameterizedTest(name = "Deleting file to path [{0}] with name [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.scenarios.UploadingTestScenarios#onlyOneValidUploadingFile")
    public void shouldDeleteValidData(String path, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        Response uploadingResponse = uploadFile(cookies, path, file);

        uploadingResponse.then().statusCode(HttpStatus.CREATED.value());
        String fullFileName = path.concat(file[0].getOriginalFilename());

        given()
                .cookies(cookies)
                .queryParam("path", fullFileName)
                .when()
                .delete("api/resource")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .cookies(cookies)
                .queryParam("path", path)
                .when()
                .delete("api/resource")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("Successful downloading of file")
    @ParameterizedTest(name = "Downloading file with path [{0}]")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.scenarios.UploadingTestScenarios#onlyOneValidUploadingFile")
    public void shouldSuccessfulDownloadFile(String path, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        Response uploadingResponse = uploadFile(cookies, path, file);
        uploadingResponse.then().statusCode(HttpStatus.CREATED.value());

        String fullFileName = path.concat(file[0].getOriginalFilename());

        Response downloadResponse = given()
                .cookies(cookies)
                .queryParam("path", fullFileName)
                .when()
                .get("api/resource/download");

        downloadResponse.then().statusCode(HttpStatus.OK.value());

        String contentType = downloadResponse.getHeader(HttpHeaders.CONTENT_TYPE);
        assertThat(MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(contentType)).isTrue();

        String contentDisposition = downloadResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION);
        assertThat(contentDisposition.contains(file[0].getOriginalFilename())).isTrue();

        byte[] downloadedBytes = downloadResponse.getBody().asByteArray();
        byte[] originalBytes = file[0].getBytes();
        assertThat(originalBytes).isEqualTo(downloadedBytes);
    }

    @DisplayName("Successful resource updating test")
    @ParameterizedTest(name = "Updating resource from path [{0}] to [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.scenarios.UpdatingTestScenarios#updatingValidScenarios")
    public void shouldSuccessfulUpdateResource(String from, String to, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        Long userId = userRepository.findByUsername(validUserToTest.username()).orElseThrow().getId();
        MetadataRequest requestFrom = metadataMapper.toRequest(userId, from);
        MetadataRequest requestTo = metadataMapper.toRequest(userId, to);

        String pathToUpload = switch (requestFrom.type()) {
            case FILE -> extractActualPath(from);
            case DIRECTORY -> from;
        };

        Response uploadingResponse = uploadFile(cookies, pathToUpload, file);
        uploadingResponse.then().statusCode(HttpStatus.CREATED.value());

        Response moveResponse = given()
                .cookies(cookies)
                .queryParam("from", from)
                .queryParam("to", to)
                .when()
                .get("api/resource/move");

        ValidatableResponse response = moveResponse.then().statusCode(HttpStatus.OK.value());
        MetadataResponse metadataResponse = extractMetadata(response);

        assertThat(metadataResponse.name()).isEqualTo(requestTo.name());
        assertThat(metadataResponse.path()).isEqualTo(requestTo.path());
        if (!requestFrom.path().equals(requestTo.path())) {
            assertThat(metadataResponse.path()).isNotEqualTo(requestFrom.path());
        }

        ValidatableResponse searchResponse = given()
                .cookies(cookies)
                .queryParam("query", requestTo.name())
                .when()
                .get("api/resource/search")
                .then()
                .statusCode(HttpStatus.OK.value());

        List<MetadataResponse> searchResponseList = extractMetadataList(searchResponse);

        assertThat(searchResponseList.size()).isEqualTo(1);
        assertThat(searchResponseList.getFirst().name()).isEqualTo(requestTo.name());
        assertThat(searchResponseList.getFirst().path()).isEqualTo(requestTo.path());
    }

    @DisplayName("Invalid or empty path with code 400 - Bad Request test")
    @ParameterizedTest(name = "Trying to update resource from path [{0}] to [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.scenarios.UpdatingTestScenarios#updatingInvalidPathsScenarios")
    public void shouldFailToUpdateResourceWithInvalidPath(String from, String to, MultipartFile[] file) {

        Map<String, String> cookies = authUserWithCookies();

        Response response = given()
                .cookies(cookies)
                .queryParam("from", from)
                .queryParam("to", to)
                .when()
                .get("api/resource/move");

        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    @DisplayName("Failed access unauthorized user with code 401 - Unauthorized test")
    @ParameterizedTest(name = "Trying to update resource from path [{0}] to [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.scenarios.UpdatingTestScenarios#oneUpdatingValidScenario")
    public void shouldFailToUpdateResourceWithUnauthorizedUser(String from, String to, MultipartFile[] file) {

        Response response = given()
                .queryParam("from", from)
                .queryParam("to", to)
                .when()
                .get("api/resource/move");

        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    @DisplayName("Failed to find content with code 404 - Not Found test")
    @ParameterizedTest(name = "Updating resource from path [{0}] to [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.scenarios.UpdatingTestScenarios#oneUpdatingValidScenario")
    public void shouldNotUpdateNotFoundResource(String from, String to, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        Response response = given()
                .cookies(cookies)
                .queryParam("from", from)
                .queryParam("to", to)
                .when()
                .get("api/resource/move");

        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    @DisplayName("Failed to update resource to existing path with code 404 - Conflict test")
    @ParameterizedTest(name = "Updating resource from path [{0}] to [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.scenarios.UpdatingTestScenarios#oneUpdatingValidScenario")
    public void shouldNotUpdateResourceAlreadyExisted(String from, String to, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        Response uploadingResponseFrom = uploadFile(cookies, extractActualPath(from), file);
        uploadingResponseFrom.then().statusCode(HttpStatus.CREATED.value());

        Response uploadingResponseTo = uploadFile(cookies, extractActualPath(to), file);
        uploadingResponseTo.then().statusCode(HttpStatus.CREATED.value());

        Response response = given()
                .cookies(cookies)
                .queryParam("from", from)
                .queryParam("to", to)
                .when()
                .get("api/resource/move");

        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }
}
