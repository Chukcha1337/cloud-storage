package com.chuckcha.cloudfilestorage;

import com.chuckcha.cloudfilestorage.dto.request.UserRegistrationRequest;
import com.chuckcha.cloudfilestorage.dto.request.UserLoginRequest;
import com.chuckcha.cloudfilestorage.dto.response.ErrorResponse;
import com.chuckcha.cloudfilestorage.dto.response.UserResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNull;


public class AuthenticationIntegrationTest extends AbstractIntegrationTest {

    @DisplayName("Valid user authentication test")
    @ParameterizedTest(name = "Authentication of user {0}")
    @MethodSource("com.chuckcha.cloudfilestorage.util.TestUsers#validLoginUsers")
    public void shouldAuthenticateValidUser(UserLoginRequest loginUser) {

        UserRegistrationRequest testUser = new UserRegistrationRequest(loginUser.username(), loginUser.password());

        given()
                .contentType(ContentType.JSON)
                .body(testUser)
                .when()
                .post("api/auth/sign-up")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        Response userLoginResponse = given()
                .contentType(ContentType.JSON).body(loginUser)
                .when().post("/api/auth/sign-in");

        UserResponse userResponse = userLoginResponse.then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(UserResponse.class);

        assertThat(userResponse
                .username())
                .isNotNull()
                .isEqualTo(loginUser.username());
    }

    @DisplayName("Invalid user authentication test")
    @ParameterizedTest(name = "Authentication of user {0}")
    @MethodSource("com.chuckcha.cloudfilestorage.util.TestUsers#invalidLoginUsers")
    public void shouldNotAuthenticateInvalidUser(UserLoginRequest loginUser) {

        Response userLoginResponse = given()
                .contentType(ContentType.JSON).body(loginUser)
                .when().post("/api/auth/sign-in");

        ErrorResponse errorResponse = userLoginResponse.then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.message()).isNotNull().isNotBlank();
    }


}
