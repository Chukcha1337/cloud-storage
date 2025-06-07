package com.chuckcha.cloudfilestorage.api;

import com.chuckcha.cloudfilestorage.dto.response.ErrorResponse;
import com.chuckcha.cloudfilestorage.integration.AbstractIntegrationTest;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class GlobalErrorTests extends AbstractIntegrationTest {

    @DisplayName("Invalid or empty path with code 400 - Bad Request test")
    @ParameterizedTest(name = "Testing [{1}] endpoint with path [{3}]")
    @MethodSource("com.chuckcha.cloudfilestorage.util.TestErrorScenarios#badRequestScenarios")
    public void shouldNotGetContentWithInvalidPath(String method, String endpoint, String param, String path) {

        RequestSpecification request = given()
                .cookies(authUserWithCookies())
                .queryParam(param, path);

        Response response = callMethod(request, method, endpoint);
        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    @DisplayName("Failed access unauthorized user with code 401 - Unauthorized test")
    @ParameterizedTest(name = "Testing [{1}] endpoint with path [{3}]")
    @MethodSource("com.chuckcha.cloudfilestorage.util.TestErrorScenarios#unauthorizedScenarios")
    public void shouldNotGetContentForUnauthorizedUser(String method, String endpoint, String param, String path) {

        RequestSpecification request = given().queryParam(param, path);

        Response response = callMethod(request, method, endpoint);
        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    @DisplayName("Failed to find content with code 404 - Not Found test")
    @ParameterizedTest(name = "Testing [{1}] endpoint with path [{3}]")
    @MethodSource("com.chuckcha.cloudfilestorage.util.TestErrorScenarios#notFoundScenarios")
    public void shouldNotGetNonExistingData(String method, String endpoint, String param, String path) {

        RequestSpecification request = given()
                .cookies(authUserWithCookies())
                .queryParam(param, path);

        Response response = callMethod(request, method, endpoint);
        ErrorResponse errorResponse = response.then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }

    private Response callMethod(RequestSpecification request, String method, String endpoint) {
        return switch (method) {
            case "GET" -> request.get(endpoint);
            case "POST" -> request.post(endpoint);
            case "DELETE" -> request.delete(endpoint);
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }

}
