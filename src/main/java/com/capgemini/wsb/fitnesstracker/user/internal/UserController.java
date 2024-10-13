package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.SimpleUserDto;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserEmailDto;
import com.capgemini.wsb.fitnesstracker.user.api.UserNotFoundException;
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

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                          .stream()
                          .map(userMapper::toDto)
                          .toList();
    }

    @GetMapping("/simple")
    public List<SimpleUserDto> getSimpleUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

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

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @GetMapping("/older/{time}")
    public List<UserDto> getUserByDate(@PathVariable String time) {
        LocalDate date = LocalDate.parse(time);
        return userService.findAllUsers()
                .stream()
                .filter(user -> user.getBirthdate().isBefore(date))
                .map(userMapper::toDto)
                .toList();
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return userMapper.toDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserDto userDto) throws InterruptedException {

        User user = userMapper.toEntity(userDto);
        User createdUser = userService.createUser(user);
        return userMapper.toDto(createdUser);
    }

}