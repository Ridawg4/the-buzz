package com.teamyellow.thebuzz.Services;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalStorage {
    public static String addToTempStorage(MultipartFile file, String filename) {
        File tempFile = new File(ResourcePaths.TEMP_DIRECTORY + filename);
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
        File tempFile = new File(ResourcePaths.LIVE_DIRECTORY + filename);
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
