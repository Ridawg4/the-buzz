package com.teamwhite.thebuzz.Exceptions;

public class MissingUserDetailsException extends RuntimeException {
    /**
     * Thrown if the UserDetails provided is null
     * @param message String error message
     */
    public MissingUserDetailsException(String message) {
        super(message);
    }
}
