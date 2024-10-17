package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.*;
import com.capgemini.wsb.fitnesstracker.user.api.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService, UserProvider {

    private final UserRepository userRepository;

    @Override
    public User createUser(final User user) {
        log.info("Creating User {}", user);
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already DB ID, update is not permitted!");
        }

        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException(user.getEmail());
        }

        return userRepository.save(user);
    }


    @Override
    public User updateUser(final Long userId, final UserDto userDto) {
        if (getUserByEmail(userDto.email()).isPresent()) {
            throw new DuplicateEmailException(userDto.email());
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException((userId)));
        log.info("Updating User {}", user);

        if(userDto.firstName() != null) user.setFirstName(userDto.firstName());
        if(userDto.lastName() != null) user.setLastName(userDto.lastName());
        if(userDto.birthdate() != null) user.setBirthdate(userDto.birthdate());
        if(userDto.email() != null) user.setEmail(userDto.email());

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUser(final Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}