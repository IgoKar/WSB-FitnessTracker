package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.*;
import com.capgemini.wsb.fitnesstracker.user.api.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 * Provides endpoints for creating, updating, fetching, and deleting users.
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    /**
     * Retrieves all users in the system.
     *
     * @return a list of UserDto representing all users.
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves simplified user data ((id, first name, last name).
     *
     * @return a list of SimpleUserDto representing all users with simplified data.
     */
    @GetMapping("/simple")
    public List<SimpleUserDto> getSimpleUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    /**
     * Retrieves user by email. If no email is provided, returns all users.
     *
     * @param email the email of the user to search for (optional).
     * @return a list of UserEmailDto representing user matching the provided email or all users if no email is specified.
     */
    @GetMapping("/email")
    public List<UserEmailDto> getUsersByEmail(@RequestParam(required = false) String email) {
        if (email != null && !email.isEmpty()) {
            Optional<User> optionalUser = userService.getUserByEmail(email);
            return optionalUser
                    .map(user -> List.of(userMapper.toUserEmailDto(user)))
                    .orElseGet(List::of);
        }
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toUserEmailDto)
                .toList();
    }

    /**
     * Retrieves users who are older than the specified date.
     *
     * @param time the date to compare against in String format (YYYY-MM-DD).
     * @return a list of UserDto representing users older than the specified date.
     */
    @GetMapping("/older/{time}")
    public List<UserDto> getUserByDate(@PathVariable String time) {
        LocalDate date = LocalDate.parse(time);
        return userService.findAllUsers()
                .stream()
                .filter(user -> user.getBirthdate().isBefore(date))
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a specific user by their id.
     *
     * @param id the id of the user to retrieve.
     * @return the UserDto representing the user with the specified id.
     * @throws UserNotFoundException if no user is found with the specified id.
     */
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Retrieves a specific user by their id in simple format.
     *
     * @param id the id of the user to retrieve.
     * @return the UserDto representing the user with the specified id.
     * @throws UserNotFoundException if no user is found with the specified id.
     */
    @GetMapping("/simple/{id}")
    public SimpleUserDto getSimpleUser(@PathVariable Long id) {
        return userService.getUser(id)
                .map(userMapper::toSimpleDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Updates an existing user identified by their id.
     *
     * @param id the id of the user to update.
     * @param userDto the data of the user to update.
     * @return the updated UserDto representing the user.
     * @throws DuplicateEmailException if the email provided is already in use.
     */
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (userService.getUserByEmail(userDto.email()).isPresent()) {
            throw new DuplicateEmailException(userDto.email());
        }
        User updatedUser = userService.updateUser(id, userDto);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Deletes a user identified by their id.
     *
     * @param id the id of the user to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    /**
     * Adds a new user to the system.
     *
     * @param userDto the data of the new user to create.
     * @return the created UserDto representing the new user.
     * @throws InterruptedException if the operation is interrupted.
     * @throws DuplicateEmailException if the email provided is already in use.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserDto userDto) throws InterruptedException {
        if (userService.getUserByEmail(userDto.email()).isPresent()) {
            throw new DuplicateEmailException(userDto.email());
        }
        User user = userMapper.toEntity(userDto);
        User createdUser = userService.createUser(user);
        return userMapper.toDto(createdUser);
    }

    /**
     * Handles UserNotFoundException by returning a 409 Conflict response.
     *
     * @param e the UserNotFoundException to handle.
     * @return a ResponseEntity with status 404 and the exception message.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Handles DuplicateEmailException by returning a 409 Conflict response.
     *
     * @param e the DuplicateEmailException to handle.
     * @return a ResponseEntity with status 409 and the exception message.
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmailException(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}