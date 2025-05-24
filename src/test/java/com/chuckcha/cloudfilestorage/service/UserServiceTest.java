package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.entity.User;
import com.chuckcha.cloudfilestorage.mapper.UserMapper;
import com.chuckcha.cloudfilestorage.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserService userService;

    @BeforeAll
    static void setUp() {

    }

    @BeforeEach
    void setup() {

    }

    @Test
    void register1() {
    }

    @Test
    void create() {
        Mockito.doReturn(Optional.of(new User()));
    }

    @Test
    void loadUserByUsername() {
    }
}