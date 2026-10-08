package com.teamwhite.thebuzz.Exceptions;

import java.io.IOException;

public class FilePathEmptyException extends IOException {
    /**
     * Thrown if the file path navigated contains no files
     * @param message error message
     */
    public FilePathEmptyException(String message) {
        super(message);
    }
}
