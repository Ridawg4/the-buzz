package com.teamwhite.thebuzz.Exceptions;

import java.io.IOException;


public class NoFilesToEncodeInPlaylist extends IOException {
    /**
     * Thrown if no files are present in the given files array for the PlaylistBuilder
     * @param message error message
     */
    public NoFilesToEncodeInPlaylist(String message) {
        super(message);
    }
}
