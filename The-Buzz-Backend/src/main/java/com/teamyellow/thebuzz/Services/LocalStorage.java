package com.teamyellow.thebuzz.Services;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class LocalStorage {

    public static String addToAudioStorage(MediaPlaylist mediaPlaylist, String filename, String filePath) {
        File tempFile = new File(filePath + ResourcePaths.SEPARATOR + filename);
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

    public static String addToAudioStorage(MultipartFile file, String filename, String filePath) {
        if(!new File(filePath).exists()) {
            File dir = new File(filePath);
            dir.mkdir();
        }
        File tempFile = new File(filePath + filename);
        try {
            tempFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tempFile);
            stream.write(file.getBytes());
            stream.close();
        } catch (IOException e) {

        }

        return tempFile.getName();
    }

    public static String addToAudioStorage(MultipartFile file, String filename, String filePath, String folderToCreate) {
        File dir = new File(filePath
                + folderToCreate + ResourcePaths.SEPARATOR);
        if(!dir.exists()) {
            dir.mkdir();
        }

        dir = new File(filePath
                + folderToCreate + ResourcePaths.SEPARATOR + "Segments" + ResourcePaths.SEPARATOR);
        if(!dir.exists()) {
            dir.mkdir();
        }
        File tempFile = new File(filePath + folderToCreate + ResourcePaths.SEPARATOR + filename);
        try {
            tempFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tempFile);
            stream.write(file.getBytes());
            stream.close();
        } catch (IOException e) {

        }

        return tempFile.getName();
    }

    public static String createEmptyFile(String filename) {
        File tempFile = new File(ResourcePaths.TEMP_DIRECTORY + filename);
        try {
            tempFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tempFile);
            stream.write("".getBytes());
            stream.close();
        } catch (IOException e) {

        }

        return tempFile.getName();
    }

    public static boolean doesFileExistAtDirectory(String pathToFile) throws FileNotFoundException {
        File file = new File(pathToFile);

        return file.exists();
    }

    public static boolean removeFileFromLocalStorage(String filename, String filePath) {
        File tempFile = new File(filePath + ResourcePaths.SEPARATOR + filename);
        return tempFile.delete();
    }
}
