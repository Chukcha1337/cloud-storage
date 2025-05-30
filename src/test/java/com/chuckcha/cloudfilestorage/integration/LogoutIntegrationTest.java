package com.chuckcha.cloudfilestorage.integration;

import com.chuckcha.cloudfilestorage.util.TestUsers;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class LogoutIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Authenticated user success (code 204) logout test")
    public void shouldLogoutAuthenticatedUser() {

        var regResponse = given()
                .contentType(ContentType.JSON)
                .body(TestUsers.ALICE)
                .when().post("/api/auth/sign-up");

        Map<String, String> cookies = regResponse.getCookies();

        var logoutRequest = given()
                .cookies(cookies);

        logoutRequest
                .when()
                .post("/api/auth/sign-out")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Unauthenticated user unsuccessful (code 401) logout test")
    public void shouldNotLogoutUnauthenticatedUser() {

        given()
                .when()
                .post("/api/auth/sign-out")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
