package com.teamwhite.thebuzz.Exceptions;

public class InvalidPermissionsObjectException extends RuntimeException {
    /**
     * Thrown if the Permissions provided is invalid
     * @param message
     * @TODO MAP AS A PARENT OF FURTHER SPECIFIED EXCEPTION
     */
    public InvalidPermissionsObjectException(String message) {
        super(message);
    }
}
