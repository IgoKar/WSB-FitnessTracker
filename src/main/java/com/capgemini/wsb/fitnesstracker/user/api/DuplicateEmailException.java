package com.capgemini.wsb.fitnesstracker.user.api;

public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException(String email) {
        super("Email " + email + " is already in use.");
    }
}
