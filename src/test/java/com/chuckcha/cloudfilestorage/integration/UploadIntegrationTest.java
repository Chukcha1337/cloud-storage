package com.chuckcha.cloudfilestorage.integration;

import com.chuckcha.cloudfilestorage.dto.request.DirectoryPathDto;
import com.chuckcha.cloudfilestorage.dto.request.MetadataRequest;
import com.chuckcha.cloudfilestorage.dto.response.ErrorResponse;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.entity.Metadata;
import com.chuckcha.cloudfilestorage.entity.Type;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class UploadIntegrationTest extends AbstractIntegrationTest {

    @DisplayName("Successful uploading resource test")
    @ParameterizedTest(name = "Uploading file to path [{0}] with name [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.util.UploadingScenarios#validUploadingScenarios")
    public void shouldUploadValidData(String path, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        Response response = uploadFile(cookies, path, file);

        ValidatableResponse validatableResponse = response
                .then()
                .statusCode(HttpStatus.CREATED.value());

        List<MetadataResponse> responseList = extractMetadataList(validatableResponse);

        assertThat(responseList.size()).isGreaterThanOrEqualTo(1);
    }


    @DisplayName("Fail to upload invalid data with code 400 - Bad Request test")
    @ParameterizedTest(name = "Uploading file to path [{0}] with name [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.util.UploadingScenarios#invalidUploadingScenarios")
    public void shouldNotUploadInvalidData(String path, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        Response response = uploadFile(cookies, path, file);

        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    @DisplayName("Failed to upload data to unauthorized user with code 401 - Unauthorized test")
    @ParameterizedTest(name = "Uploading file to path [{0}] with name [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.util.UploadingScenarios#onlyOneValidUploadingFile")
    public void shouldNotUploadValidDataWithUnauthorizedUser(String path, MultipartFile[] file) throws IOException {

        RequestSpecification request = given()
                .queryParam("path", path);

        for (MultipartFile f : file) {
            request = request.multiPart("files", f.getOriginalFilename(), f.getInputStream());
        }

        Response response = request.post("api/resource");
        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    @DisplayName("Failed to upload duplicate data with code 409 - Conflict test")
    @ParameterizedTest(name = "Uploading file to path [{0}] with name [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.util.UploadingScenarios#onlyOneValidUploadingFile")
    public void shouldNotUploadDuplicateValidData(String path, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        uploadFile(cookies, path, file);

        Response response = uploadFile(cookies, path, file);

        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();

    }
}

