package com.chuckcha.cloudfilestorage.testdata.data;

import com.chuckcha.cloudfilestorage.dto.request.user.UserRegistrationRequest;
import com.chuckcha.cloudfilestorage.dto.request.user.UserLoginRequest;

import java.util.stream.Stream;

public class TestUsers {

    // Valid users
    public static final UserRegistrationRequest ALICE =
            new UserRegistrationRequest("alice", "StrongPass1!");
    public static final UserRegistrationRequest FYODOR =
            new UserRegistrationRequest("fyodor", "Secret123!");
    public static final UserRegistrationRequest CHARLIE =
            new UserRegistrationRequest("charlie", "Password456");

    // Invalid users
    public static final UserRegistrationRequest EMPTY_USERNAME =
            new UserRegistrationRequest("", "ValidPass1!");
    public static final UserRegistrationRequest SHORT_USERNAME =
            new UserRegistrationRequest("abc", "ValidPass1!");
    public static final UserRegistrationRequest INVALID_USERNAME_PATTERN =
            new UserRegistrationRequest("!nvalid$", "ValidPass1!");
    public static final UserRegistrationRequest EMPTY_PASSWORD =
            new UserRegistrationRequest("validname", "");
    public static final UserRegistrationRequest SHORT_PASSWORD =
            new UserRegistrationRequest("validname", "abc");
    public static final UserRegistrationRequest INVALID_PASSWORD_PATTERN =
            new UserRegistrationRequest("validname", "bad password<>");

    // User's streams
    public static Stream<UserRegistrationRequest> validUsers() {
        return Stream.of(ALICE, FYODOR, CHARLIE);
    }

    public static Stream<UserRegistrationRequest> invalidUsers() {
        return Stream.of(
                EMPTY_USERNAME,
                SHORT_USERNAME,
                INVALID_USERNAME_PATTERN,
                EMPTY_PASSWORD,
                SHORT_PASSWORD,
                INVALID_PASSWORD_PATTERN
        );
    }

    public static Stream<UserLoginRequest> validLoginUsers() {
        return validUsers()
                .map(r -> new UserLoginRequest(r.username(), r.password()));
    }

    public static Stream<UserLoginRequest> invalidLoginUsers() {
        return invalidUsers()
                .map(r -> new UserLoginRequest(r.username(), r.password()));
    }

    public static UserLoginRequest toLoginUser(UserRegistrationRequest userRegistrationRequest) {
        return new UserLoginRequest(userRegistrationRequest.username(), userRegistrationRequest.password());
    }

}
