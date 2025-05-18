package com.chuckcha.cloudfilestorage.service;

import com.chuckcha.cloudfilestorage.dto.UserCreateDto;
import com.chuckcha.cloudfilestorage.dto.UsernameResponseDto;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Transactional
    public UsernameResponseDto register1(UserCreateDto userCreateDto) {
        if (userRepository.findByUsername(userCreateDto.getUsername()).isPresent()) {
            throw new DuplicateKeyException("User with name %s already exists".formatted(userCreateDto.getUsername()));
        }
        User user = mapper.toEntity(userCreateDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return mapper.toDto(savedUser);
    }

    @Transactional
    public UsernameResponseDto create(UserCreateDto userCreateDto) {
        return Optional.of(userCreateDto)
                .map(mapper::toEntity)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(userCreateDto.getRawPassword()));
                    return user;
                })
                .map(userRepository::save)
                .map(mapper::toDto)
                .orElseThrow();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }
}
