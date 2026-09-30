package com.teamyellow.thebuzz.Services;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

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
        File dir = new File(filePath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(filePath + "Segments" + ResourcePaths.SEPARATOR);
        if(!dir.exists()) {
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

    public static boolean removeFilesFromLocalStorage(String filePath) {
        File dir = new File(filePath);

        if(dir.exists()) {
            for(File file : Objects.requireNonNull(dir.listFiles())) {
                    file.delete();
                    // @TODO will need error handling in chance that files arent deleted
            }
        } else {

        }
        return true;
    }

    public static boolean removeFilesFromLocalStorage(String filePath, String[] exclusions) {
        File dir = new File(filePath);

        if(dir.exists()) {
            for(File file : Objects.requireNonNull(dir.listFiles())) {
                boolean skipFile = false;

                for(String excluded : exclusions) {
                    if (file.getName().equals(excluded)) {
                        skipFile = true;
                        break;
                    }
                }

                if(!skipFile) {
                    file.delete();
                }
            }
        } else {

        }
        return true;
    }

    public static Optional<Resource> retrieveFileFromLocalStorage(String path, String filename) throws MalformedURLException {
        File fileToReturn = new File(path + ResourcePaths.SEPARATOR + filename);
        if(fileToReturn.exists()) {
            return Optional.of(new UrlResource( fileToReturn.toURI()));
        } else {
            return Optional.empty();
        }
    }
}
