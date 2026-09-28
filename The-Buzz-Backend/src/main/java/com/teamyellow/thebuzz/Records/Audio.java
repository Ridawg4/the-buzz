package com.teamyellow.thebuzz.Records;

public class Audio {
    String fileName;
    Byte[] data;

    public Audio(String fileName, Byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }
}
