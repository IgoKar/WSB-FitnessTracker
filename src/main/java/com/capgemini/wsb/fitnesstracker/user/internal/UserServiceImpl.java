package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.*;
import com.capgemini.wsb.fitnesstracker.user.api.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for CRUD operations.
 */

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService, UserProvider {

    private final UserRepository userRepository;

    /**
     * Creates a new user in the system.
     *
     * @param user the User entity to be created.
     * @return the created User entity.
     * @throws IllegalArgumentException if the User with this id already exists in the database.
     */
    @Override
    public User createUser(final User user) {
        log.info("Creating User {}", user);
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already DB ID, update is not permitted!");
        }

        return userRepository.save(user);
    }

    /**
     * Updates an existing user in the system.
     *
     * @param userId  the ID of the user to be updated.
     * @param userDto the data to update the user with.
     * @return the updated User entity.
     * @throws UserNotFoundException if no user with the specified id is found.
     */
    @Override
    public User updateUser(final Long userId, final UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        log.info("Updating User {}", user);

        if (userDto.firstName() != null) user.setFirstName(userDto.firstName());
        if (userDto.lastName() != null) user.setLastName(userDto.lastName());
        if (userDto.birthdate() != null) user.setBirthdate(userDto.birthdate());
        if (userDto.email() != null) user.setEmail(userDto.email());

        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their id.
     *
     * @param userId the id of the user to retrieve.
     * @return an Optional containing the User if found, or an empty Optional if not found.
     */
    @Override
    public Optional<User> getUser(final Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email of the user to retrieve.
     * @return an Optional containing the User if found, or an empty Optional if not found.
     */
    @Override
    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(final Long userId) {
        // Check if the user exists before deleting
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)); // Handle case where user doesn't exist
        log.info("Deleting User with ID {}", userId);
        userRepository.delete(user); // Delete the user
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a List of all User entities.
     */
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}