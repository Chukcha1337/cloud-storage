package com.chuckcha.cloudfilestorage.api.integration.user;

import com.chuckcha.cloudfilestorage.api.integration.AbstractIntegrationTest;
import com.chuckcha.cloudfilestorage.dto.response.UserResponse;
import com.chuckcha.cloudfilestorage.testdata.data.TestUsers;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class UserIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Getting authenticated user by accessing '/me' test")
    public void shouldFindAuthenticatedUserByAccessingMe() {

        var regResponse = given()
                .contentType(ContentType.JSON)
                .body(TestUsers.ALICE)
                .when().post("/api/auth/sign-up");

        Map<String, String> cookies = regResponse.getCookies();

        var userRequest = given()
                .cookies(cookies);

        var response = userRequest
                .when()
                .get("/api/user/me")
                .then()
                .statusCode(HttpStatus.OK.value());

        assertThat(response.extract().as(UserResponse.class).username()).isEqualTo(TestUsers.ALICE.username());
    }

    @Test
    @DisplayName("Fail to get unauthenticated user by accessing '/me' test")
    public void shouldNotFindUnauthenticatedUserByAccessingMe() {

        given()
                .when()
                .get("/api/user/me")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
