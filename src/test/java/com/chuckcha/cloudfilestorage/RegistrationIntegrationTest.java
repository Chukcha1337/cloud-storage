package com.chuckcha.cloudfilestorage;

import com.chuckcha.cloudfilestorage.dto.request.UserRegistrationRequest;
import com.chuckcha.cloudfilestorage.dto.response.ErrorResponse;
import com.chuckcha.cloudfilestorage.dto.response.UserResponse;
import com.chuckcha.cloudfilestorage.entity.User;
import com.chuckcha.cloudfilestorage.util.TestUsers;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.testcontainers.shaded.org.hamcrest.Matchers.not;

public class RegistrationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("Successful valid user registration test")
    @ParameterizedTest(name = "Registration of user {0}")
    @MethodSource("com.chuckcha.cloudfilestorage.util.TestUsers#validUsers")
    public void shouldRegisterNewUserSuccessfully(UserRegistrationRequest user) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/api/auth/sign-up");

        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body("username", equalTo(user.username()))
                .cookie("SESSION", notNullValue());

        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        assertThat(userResponse.username()).isEqualTo(user.username());
    }

    @DisplayName("Unsuccessful invalid user registration test")
    @ParameterizedTest(name = "Registration of user {0}")
    @MethodSource("com.chuckcha.cloudfilestorage.util.TestUsers#invalidUsers")
    public void shouldNotRegisterNewInvalidUser(UserRegistrationRequest user) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/api/auth/sign-up");

        ErrorResponse errorResponse = response.then().statusCode(HttpStatus.BAD_REQUEST.value()).extract().as(ErrorResponse.class);
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.message()).isNotNull();

        assertNull(response.getCookie("SESSION"));
    }

    @Test
    @DisplayName("Duplicate registration test")
    public void mustNotRegisterDuplicateUser() {
        UserRegistrationRequest testUser = TestUsers.ALICE;

        given().contentType(ContentType.JSON).body(testUser)
                .when().post("/api/auth/sign-up")
                .then().statusCode(HttpStatus.CREATED.value());

        given().contentType(ContentType.JSON).body(testUser)
                .when().post("/api/auth/sign-up")
                .then().statusCode(HttpStatus.CONFLICT.value());


    }

    @Test
    @DisplayName("Registered user database existence test")
    public void registeredUserMustExistAtDatabase() {
        UserRegistrationRequest testUser = TestUsers.ALICE;

        given().contentType(ContentType.JSON).body(testUser)
                .when().post("/api/auth/sign-up")
                .then().statusCode(HttpStatus.CREATED.value());

        assertThat(userRepository.findByUsername(testUser.username()).isPresent()).isTrue();
    }

    @Test
    @DisplayName("Hashed database password test")
    public void mustSaveHashedPasswordAtDatabase() {
        UserRegistrationRequest testUser = TestUsers.ALICE;

        given().contentType(ContentType.JSON).body(testUser)
                .when().post("/api/auth/sign-up")
                .then().statusCode(HttpStatus.CREATED.value());

        User userResponse = userRepository.findByUsername(testUser.username()).orElseThrow();
        assertThat(userResponse.getPassword()).isNotEqualTo(testUser.rawPassword());
        assertThat(passwordEncoder.matches(testUser.rawPassword(), userResponse.getPassword())).isTrue();
    }
}
