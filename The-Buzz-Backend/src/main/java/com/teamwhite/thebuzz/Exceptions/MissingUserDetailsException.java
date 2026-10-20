package com.teamwhite.thebuzz.Exceptions;

public class MissingUserDetailsException extends RuntimeException {
    public MissingUserDetailsException(String message) {
        super(message);
    }
}
