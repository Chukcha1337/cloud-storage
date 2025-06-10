package com.chuckcha.cloudfilestorage.api.integration;

import com.chuckcha.cloudfilestorage.config.TestcontainersConfiguration;
import com.chuckcha.cloudfilestorage.dto.request.user.UserRegistrationRequest;
import com.chuckcha.cloudfilestorage.dto.response.MetadataResponse;
import com.chuckcha.cloudfilestorage.mapper.MetadataMapper;
import com.chuckcha.cloudfilestorage.repository.MetadataRepository;
import com.chuckcha.cloudfilestorage.repository.UserRepository;
import com.chuckcha.cloudfilestorage.service.MetadataService;
import com.chuckcha.cloudfilestorage.service.ResourceService;
import com.chuckcha.cloudfilestorage.testdata.data.TestPaths;
import com.chuckcha.cloudfilestorage.testdata.data.TestUsers;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        // minio url
        String minioUrl = "http://" + TestcontainersConfiguration.minioContainer.getHost()
                          + ":" + TestcontainersConfiguration.minioContainer.getFirstMappedPort();

        registry.add("minio.url", () -> minioUrl);
        registry.add("minio.access-key", () -> "minioaccesskey");
        registry.add("minio.secret-key", () -> "miniosecretkey");
    }

    @LocalServerPort
    protected int port;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MetadataRepository metadataRepository;

    @Autowired
    protected MetadataService metadataService;

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected MetadataMapper metadataMapper;

    protected final UserRegistrationRequest validUserToTest = TestUsers.ALICE;
    protected final String validDirectoryPath = TestPaths.FOLDER_SIMPLE.path();

    @BeforeEach
    void setupRestAssured() {
        RestAssured.port = port;
    }

    @AfterEach
    void clearDatabase() {
        userRepository.deleteAll();
        metadataRepository.deleteAll();
    }

    protected Map<String, String> authUserWithCookies() {
        return given()
                .contentType(ContentType.JSON)
                .body(validUserToTest)
                .when().post("/api/auth/sign-up")
                .getCookies();
    }

    protected List<MetadataResponse> extractMetadataList(ValidatableResponse response) {
        return response.extract().body().jsonPath().getList(".", MetadataResponse.class);
    }

    protected MetadataResponse extractMetadata(ValidatableResponse response) {
        return response.extract().as(MetadataResponse.class);
    }

    protected Response uploadFile(Map<String, String> cookies, String path, MultipartFile[] file) throws IOException {

        RequestSpecification request = given()
                .cookies(cookies)
                .queryParam("path", path);

        for (MultipartFile f : file) {
            request = request.multiPart("files", f.getOriginalFilename(), f.getInputStream());
        }

        Response response = request.when()
                .post("api/resource");
        return response;
    }
}
