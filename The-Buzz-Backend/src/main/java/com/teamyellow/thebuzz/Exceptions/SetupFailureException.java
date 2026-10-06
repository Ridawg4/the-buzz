package com.teamyellow.thebuzz.Exceptions;

public class SetupFailureException extends RuntimeException{
    /**
     * Thrown if the setup of the application failed
     * @param message error message
     */
    public SetupFailureException(String message) {
        super(message);
    }
}
