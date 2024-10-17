package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.*;
import com.capgemini.wsb.fitnesstracker.user.api.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    // --- READ: Get all users ---
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    // --- READ: Get simplified user data (only some fields) ---
    @GetMapping("/simple")
    public List<SimpleUserDto> getSimpleUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    // --- READ: Get users by email, if email is provided ---
    @GetMapping("/email")
    public List<UserEmailDto> getUsersByEmail(@RequestParam(required = false) String email) {
        if (email != null && !email.isEmpty()) {
            return userService.getUserByEmail(email)
                    .stream()
                    .map(userMapper::toUserEmailDto)
                    .toList();
        }
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toUserEmailDto)
                .toList();
    }

    // --- READ: Get users older than the specified date ---
    @GetMapping("/older/{time}")
    public List<UserDto> getUserByDate(@PathVariable String time) {
        LocalDate date = LocalDate.parse(time);
        return userService.findAllUsers()
                .stream()
                .filter(user -> user.getBirthdate().isBefore(date))
                .map(userMapper::toDto)
                .toList();
    }

    // --- READ: Get a specific user by ID ---
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        // Retrieves a user by their ID, or throws a UserNotFoundException if the user doesn't exist
        return userService.getUser(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // --- UPDATE: Update an existing user ---
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (userService.getUserByEmail(userDto.email()).isPresent()) {
            throw new DuplicateEmailException(userDto.email());
        }
        User updatedUser = userService.updateUser(id, userDto);
        return userMapper.toDto(updatedUser);
    }

    // --- DELETE: Delete a user by their ID ---
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    // --- CREATE: Add a new user ---
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
}