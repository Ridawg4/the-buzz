package com.teamwhite.thebuzz.Exceptions;

public class NoUserProvidedException extends RuntimeException {
    /**
     * Thrown if a user was null when specified to always contain a value
     * @param message String error message
     */
    public NoUserProvidedException(String message) {
        super(message);
    }
}
