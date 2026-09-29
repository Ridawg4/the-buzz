package com.teamyellow.thebuzz.Services;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

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

    public static String addToTempStorage(MultipartFile file, String filename, String folderToCreate) {
        if(!new File(ResourcePaths.TEMP_DIRECTORY + folderToCreate + ResourcePaths.SEPARATOR).exists()) {
            File dir = new File(ResourcePaths.TEMP_DIRECTORY
                    + folderToCreate + ResourcePaths.SEPARATOR);
            dir.mkdir();
        }
        if(!new File(ResourcePaths.TEMP_DIRECTORY
                + folderToCreate + ResourcePaths.SEPARATOR + "Segments" + ResourcePaths.SEPARATOR).exists()) {
            File dir = new File(ResourcePaths.TEMP_DIRECTORY
                    + folderToCreate + ResourcePaths.SEPARATOR + "Segments" + ResourcePaths.SEPARATOR);
            dir.mkdir();
        }
        File tempFile = new File(ResourcePaths.TEMP_DIRECTORY + folderToCreate + ResourcePaths.SEPARATOR + filename);
        try {
            tempFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tempFile);
            stream.write(file.getBytes());
            stream.close();
        } catch (IOException e) {

        }

        return tempFile.getName();
    }

    public static String addToTempStorage(MediaPlaylist mediaPlaylist, String filename) {
        File tempFile = new File(ResourcePaths.TEMP_DIRECTORY + filename);
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

    public static String addToStorage(MediaPlaylist mediaPlaylist, String filename, String filePath) {
        File tempFile = new File(ResourcePaths.WORKING_DIRECTORY + filePath + ResourcePaths.SEPARATOR + filename);
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

    public static String addToTempSegmentsStorage(MediaPlaylist mediaPlaylist, String filename) {
        File tempFile = new File(ResourcePaths.TEMP_SEGMENTS_DIRECTORY + filename);
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

    public static boolean removeFileFromLocalStorage(String filename, String filePath) {
        File tempFile = new File(filePath + ResourcePaths.SEPARATOR + filename);
        return tempFile.delete();
    }
}
