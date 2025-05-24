package com.chuckcha.cloudfilestorage;

import com.chuckcha.cloudfilestorage.config.TestcontainersConfiguration;
import com.chuckcha.cloudfilestorage.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected UserRepository userRepository;

    @BeforeEach
    void setupRestAssured() {
        RestAssured.port = port;
    }

    @AfterEach
    void clearDatabase() {
        userRepository.deleteAll();
    }
}
