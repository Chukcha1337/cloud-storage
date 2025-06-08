package com.chuckcha.cloudfilestorage.api.integration.resource;

import com.chuckcha.cloudfilestorage.api.integration.AbstractIntegrationTest;
import com.chuckcha.cloudfilestorage.dto.request.path.DirectoryPathDto;
import com.chuckcha.cloudfilestorage.dto.request.path.MetadataRequest;
import com.chuckcha.cloudfilestorage.dto.response.ErrorResponse;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.entity.Metadata;
import com.chuckcha.cloudfilestorage.entity.Type;
import com.chuckcha.cloudfilestorage.testdata.data.TestFiles;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class DirectoryIntegrationTest extends AbstractIntegrationTest {

    @DisplayName("Successful creating new empty folder test")
    @ParameterizedTest(name = "Creating folder with name {0}")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.data.TestPaths#validDirectoryPaths")
    public void shouldCreateNewEmptyFolderSuccessfully(DirectoryPathDto path) {

        Map<String, String> cookies = authUserWithCookies();

        Long userId = userRepository.findByUsername(validUserToTest.username()).orElseThrow().getId();
        MetadataRequest request = metadataMapper.toRequest(userId, path.path());

        given().cookies(cookies)
                .queryParam("path", path.path())
                .when()
                .post("api/directory")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        Optional<Metadata> newFolder = metadataRepository.findByPathAndNameAndType(request.path(), request.name(), Type.DIRECTORY);
        assertThat(newFolder.isPresent()).isTrue();
        assertThat(newFolder.get().getName()).isEqualTo(request.name());
        assertThat(newFolder.get().getPath()).isEqualTo(request.path());
        assertThat(newFolder.get().getType()).isEqualTo(Type.DIRECTORY);
    }

    @Test
    @DisplayName("Failed to create duplicate folder with code 409 test")
    public void shouldNotCreateNewFolderWithDuplicateName() {

        Map<String, String> cookies = authUserWithCookies();

        given().cookies(cookies)
                .queryParam("path", validDirectoryPath)
                .when()
                .post("api/directory")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        var response = given().cookies(cookies)
                .queryParam("path", validDirectoryPath)
                .when()
                .post("api/directory");

        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.CONFLICT.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("Successful getting information about folder content test")
    public void shouldGetInformationAboutFolderContent() {

        Map<String, String> cookies = authUserWithCookies();

        Long userId = userRepository.findByUsername(validUserToTest.username()).orElseThrow().getId();
        MetadataRequest request = metadataMapper.toRequest(userId, validDirectoryPath);

        metadataService.save(request.fullPath().concat(TestFiles.TEXT_FILE.getOriginalFilename()), TestFiles.TEXT_FILE);
        metadataService.save(request.fullPath().concat(TestFiles.PDF_FILE.getOriginalFilename()), TestFiles.PDF_FILE);
        metadataService.createFolderIfNotExists(request.fullPath(), "emptyFolder");

        ValidatableResponse response = given()
                .cookies(cookies)
                .queryParam("path", validDirectoryPath)
                .when()
                .get("api/directory")
                .then()
                .statusCode(HttpStatus.OK.value());

        List<MetadataResponse> responseList = extractMetadataList(response);

        assertThat(responseList).hasSize(3);
    }
}
