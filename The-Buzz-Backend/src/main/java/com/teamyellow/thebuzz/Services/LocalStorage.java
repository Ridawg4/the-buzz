package com.teamyellow.thebuzz.Services;

import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class LocalStorage {
    private static final String LOCAL_DIRECTORY = System.getenv("PWD");

    public static String addToTempStorage(MultipartFile file, String filename) {
        File tempFile = new File(Path.of(LOCAL_DIRECTORY + File.separator
                + "TEMP" + File.separator + filename).toUri());
        try {
            tempFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tempFile);
            stream.write(file.getBytes());
            stream.close();
        } catch (IOException e) {

        }

        return tempFile.getName();
    }

    public static String addToLiveStorage(MediaPlaylist mediaPlaylist, String filename) {
        File tempFile = new File(Path.of(LOCAL_DIRECTORY + File.separator
                + "Live" + File.separator + filename).toUri());
        try {
            tempFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tempFile);
            MediaPlaylistParser parser = new MediaPlaylistParser();

            stream.write(parser.writePlaylistAsBytes(mediaPlaylist));
            stream.close();
        } catch (IOException e) {

        }

        return tempFile.getName();
    }
}
