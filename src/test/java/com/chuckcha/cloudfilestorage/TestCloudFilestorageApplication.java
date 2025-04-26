package com.chuckcha.cloudfilestorage;

import org.springframework.boot.SpringApplication;

public class TestCloudFilestorageApplication {

    public static void main(String[] args) {
        SpringApplication.from(CloudFilestorageApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
