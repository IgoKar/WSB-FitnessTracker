package com.capgemini.wsb.fitnesstracker.user.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException(String email) {
        super("Email " + email + " is already in use.");
    }
}
