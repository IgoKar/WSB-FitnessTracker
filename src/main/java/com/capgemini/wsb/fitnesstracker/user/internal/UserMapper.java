package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.SimpleUserDto;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserDto;
import com.capgemini.wsb.fitnesstracker.user.api.UserEmailDto;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between User entity and various DTOs.
 */
@Component
class UserMapper {

    /**
     * Converts a User entity to a UserDto.
     *
     * @param user the User entity to convert.
     * @return the corresponding UserDto.
     */
    UserDto toDto(User user) {
        return new UserDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthdate(),
                user.getEmail());
    }

    /**
     * Converts a User entity to a SimpleUserDto, containing only a subset of fields.
     *
     * @param user the User entity to convert.
     * @return the corresponding SimpleUserDto with basic user information.
     */
    SimpleUserDto toSimpleDto(User user) {
        return new SimpleUserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName());
    }

    /**
     * Converts a User entity to a UserEmailDto, containing only the email and ID.
     *
     * @param user the User entity to convert.
     * @return the corresponding UserEmailDto with email and ID.
     */
    UserEmailDto toUserEmailDto(User user) {
        return new UserEmailDto(
                user.getId(),
                user.getEmail());
    }

    /**
     * Converts a UserDto to a User entity.
     *
     * @param userDto the UserDto to convert.
     * @return the corresponding User entity.
     */
    User toEntity(UserDto userDto) {
        return new User(
                userDto.firstName(),
                userDto.lastName(),
                userDto.birthdate(),
                userDto.email());
    }
}