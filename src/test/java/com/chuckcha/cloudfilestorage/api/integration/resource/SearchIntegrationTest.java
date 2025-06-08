package com.chuckcha.cloudfilestorage.api.integration.resource;

import com.chuckcha.cloudfilestorage.api.integration.AbstractIntegrationTest;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.entity.Type;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class SearchIntegrationTest extends AbstractIntegrationTest {

    @DisplayName("Successful searching of valid data test")
    @ParameterizedTest(name = "Searching data with name [{1}]")
    @MethodSource("com.chuckcha.cloudfilestorage.testdata.scenarios.UploadingTestScenarios#onlyOneValidUploadingFile")
    public void shouldFindValidData(String path, MultipartFile[] file) throws IOException {

        Map<String, String> cookies = authUserWithCookies();

        Response uploadingResponse = uploadFile(cookies, path, file);

        uploadingResponse.then().statusCode(HttpStatus.CREATED.value());

        Response response = given()
                .cookies(cookies)
                .queryParam("query", file[0].getOriginalFilename())
                .when()
                .get("api/resource/search");

        ValidatableResponse validatableResponse = response
                .then()
                .statusCode(HttpStatus.OK.value());

        List<MetadataResponse> responseList = extractMetadataList(validatableResponse);

        assertThat(responseList.size()).isEqualTo(1);
        assertThat(responseList.getFirst().name()).isEqualTo(file[0].getOriginalFilename());
        assertThat(responseList.getFirst().size()).isEqualTo(file[0].getSize());
        assertThat(responseList.getFirst().type()).isEqualTo(Type.FILE);
    }
}
