package com.teamyellow.thebuzz.Exceptions;

import java.io.IOException;

public class FilePathEmptyException extends IOException {
    public FilePathEmptyException(String message) {
        super(message);
    }
}
