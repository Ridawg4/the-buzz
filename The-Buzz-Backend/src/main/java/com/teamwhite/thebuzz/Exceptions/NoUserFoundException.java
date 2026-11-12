package com.teamwhite.thebuzz.Exceptions;

import java.io.IOException;

public class NoUserFoundException extends IOException {
    public NoUserFoundException(String message) {
        super(message);
    }
}
