package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.SimpleUserDto;
import com.capgemini.wsb.fitnesstracker.user.api.UserDto;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserEmailDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserServiceTest {

    private MockMvc mockMvc;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() throws Exception {
        try(var mocks = MockitoAnnotations.openMocks(this)){
            mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        }
    }

    @Test
    void shouldReturnCorrectUser() throws Exception {
        long userId = 1L;
        User user = createMockUser(userId, "John", "Doe", "1990-01-01", "john.doe@example.com");

        mockUserService(userId, user);

        mockMvc.perform(get("/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.birthdate").value("1990-01-01"));
    }

    @Test
    void shouldHandleUserNotFound() throws Exception {

        mockMvc.perform(get("/v1/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void shouldReturnUserWhenEmailIsSpecified() throws Exception {
        // Given
        String email = "john.doe@example.com";
        User user = createMockUser(1L, "John", "Doe", "1990-01-01", email);

        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toUserEmailDto(user)).thenReturn(new UserEmailDto(user.getId(), user.getEmail()));

        // When & Then
        mockMvc.perform(get("/v1/users/email").param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value(email));
    }

    @Test
    void shouldReturnAllUsersWhenEmailNotSpecified() throws Exception {
        User user1 = createRandomMockUser(1L);
        User user2 = createRandomMockUser(2L);

        when(userService.findAllUsers()).thenReturn(List.of(user1, user2));
        when(userMapper.toUserEmailDto(user1)).thenReturn(new UserEmailDto(1L, user1.getEmail()));
        when(userMapper.toUserEmailDto(user2)).thenReturn(new UserEmailDto(2L, user2.getEmail()));

        mockMvc.perform(get("/v1/users/email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()));
    }

    @Test
    void shouldReturnConflictWhenDuplicateEmailExceptionThrown() throws Exception {
        Long userId = 1L;
        String duplicateEmail = "john.doe@example.com";

        User user = createMockUser(userId, "John", "Doe", "1990-01-10", duplicateEmail);

        when(userService.getUserByEmail(duplicateEmail)).thenReturn(Optional.of(user));

        String userPostRequest = """
                                                 
                {
                "firstName": "%s",
                "lastName": "%s",
                "birthdate": "%s",
                "email": "%s"
                }
                """.formatted(
                "Bob",
                "Smith",
                "1990-01-01",
                duplicateEmail);

        mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userPostRequest))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email " + duplicateEmail + " is already in use."));
    }

    @Test
    void shouldReturnSimpleUser() throws Exception {
        long userId = 1L;
        User simpleUser = createMockUser(userId, "Jane", "Smith", "1999-06-15", "janesmith@mail.com");
        SimpleUserDto simpleUserDto = new SimpleUserDto(userId, "Jane", "Smith");

        when(userService.findAllUsers()).thenReturn(List.of(simpleUser));
        when(userMapper.toSimpleDto(simpleUser)).thenReturn(simpleUserDto);

        mockMvc.perform(get("/v1/users/simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect((jsonPath("$[0].id").value(userId)))
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"));
    }

    private User createMockUser (Long id, String firstName, String lastName, String birthdate, String email) {
        User user = new User(firstName, lastName, LocalDate.parse(birthdate), email);
        user.setId(id);
        return user;
    }

    private User createRandomMockUser(Long id) {
            User user = new User(randomUUID().toString(), randomUUID().toString(), LocalDate.now(), randomUUID().toString());
            user.setId(id);
            return user;
    }

    private void mockUserService(Long userId, User user) {
        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getBirthdate(), user.getEmail()));
    }
}
