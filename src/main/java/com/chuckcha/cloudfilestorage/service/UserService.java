package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.dto.request.UserRegistrationRequest;
import com.chuckcha.cloudfilestorage.dto.response.UserResponse;
import com.chuckcha.cloudfilestorage.entity.User;
import com.chuckcha.cloudfilestorage.mapper.UserMapper;
import com.chuckcha.cloudfilestorage.repository.UserRepository;
import com.chuckcha.cloudfilestorage.security.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Transactional
    public UserResponse create(UserRegistrationRequest dto) {
        userRepository.findByUsername(dto.username()).ifPresent(user -> {
            throw new DuplicateKeyException("User with name %s already exists".formatted(dto.username()));
        });

        User user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.rawPassword()));

        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }
}
