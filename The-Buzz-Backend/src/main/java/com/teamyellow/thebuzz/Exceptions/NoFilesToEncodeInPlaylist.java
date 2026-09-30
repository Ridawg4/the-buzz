package com.teamyellow.thebuzz.Exceptions;

import java.io.IOException;

public class NoFilesToEncodeInPlaylist extends IOException {
    public NoFilesToEncodeInPlaylist(String message) {
        super(message);
    }
}
