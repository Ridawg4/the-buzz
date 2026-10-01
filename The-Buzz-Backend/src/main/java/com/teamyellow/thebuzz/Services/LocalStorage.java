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

    /**
     * Adds the MediaPlaylist file to the associated file path with the denoted filename
     * <p>
     * Created specifically for Audio processing pipeline
     * </p>
     * <p>
     * Does not include any separators, the path should end with a separator
     *</p>
     * @param mediaPlaylist Playlist file to save
     * @param filename Name of the file to save
     * @param filePath Path to save the file
     * @return String filename
     */
    public static String addToAudioStorage(MediaPlaylist mediaPlaylist, String filename, String filePath) {
        File tempFile = new File(filePath + filename);
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

    /**
     * Adds the MutlipartFile file to the associated file path with the denoted filename
     * <p>
     * Created specifically for Audio processing pipeline
     * </p>
     * <p>
     * Does not include any separators, the path should end with a separator
     *</p>
     * @param file File to save
     * @param filename Name of the file to save
     * @param filePath Path to save the file
     * @return String filename
     */
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
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
        }

        return tempFile.getName();
    }

    /**
     * Adds the MutlipartFile file to the associated file path and folder pathway
     * with the denoted filename
     * <p>
     * Created specifically for Audio processing pipeline
     * </p>
     * <p>
     * Does not include any separators, the path should end with a separator
     *</p>
     * @param file File to save
     * @param filename Name of the file to save
     * @param filePath Path to save the file
     * @param folderToCreate The folder to place content inside of
     * @return String filename
     */
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
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
        }

        return tempFile.getName();
    }

    /**
     * Creates an empty file at the given filepath with the given filename
     * <p>
     * Does not include any separators, the path should end with a separator
     *</p>
     * @param filename Name of the file to create
     * @param path Pathway to create the file in
     * @return String the name of the created file
     */
    public static String createEmptyFile(String filename, String path) {
        File tempFile = new File(path + filename);
        try {
            tempFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tempFile);
            stream.write("".getBytes());
            stream.close();
        } catch (IOException e) {

        }

        return tempFile.getName();
    }

    /**
     * Checks if a file at a given path
     * <p>
     * Does not include any separators, the path should end with a separator
     *</p>
     * @param path Path to check for the file
     * @param fileName Name of the file to check for
     * @return boolean Does the file exist?
     */
    public static boolean doesFileExistAtDirectory(String path, String fileName) {
        File file = new File(path + fileName);

        return file.exists();
    }

    /**
     * Deletes a file at the given path with the given filename
     *
     * @param filename Name of the file to delete
     * @param filePath Path the files exists within
     * @return boolean Was deletion successful?
     */
    public static boolean removeFileFromLocalStorage(String filename, String filePath) {
        File tempFile = new File(filePath + filename);
        return tempFile.delete();
    }

    /**
     * Deletes all files within a given file path
     * <p>
     * THIS IS NOT A REVERSIBLE ACTION AND MODIFIES SYSTEM STORAGE AT RUNTIME
     * STICK WITHIN ResourcePaths DEFINED DIRECTORIES OR UNDESIRED DELETIONS
     * INSIDE YOUR FILE SYSTEM MAY OCCUR
     *</p>
     * @param filePath Path to folder to delete all files within
     * @return boolean Was deletion successful?
     */
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

    /**
     * Deletes all files within a given file path, ignoring files with names that are excluded as
     * provided
     * <p>
     * THIS IS NOT A REVERSIBLE ACTION AND MODIFIES SYSTEM STORAGE AT RUNTIME
     * STICK WITHIN ResourcePaths DEFINED DIRECTORIES OR UNDESIRED DELETIONS
     * INSIDE YOUR FILE SYSTEM MAY OCCUR
     *</p>
     * @param filePath Path to folder to delete all files within
     * @param exclusions A String array containing filenames of files to not delete within a directory
     * @return boolean Was deletion successful?
     */
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

    /**
     * Attempts to retrieve a file resource from local storage based on provided path and filename
     * Does not include any separators, the path should end with a separator
     *
     * @param path Pathway to the file requested
     * @param filename name of the file requested
     * @return Optional<Resource> File that may or may not be present
     * @throws MalformedURLException If the path & filename are invalid or not present
     */
    public static Optional<Resource> retrieveFileFromLocalStorage(String path, String filename) throws MalformedURLException {
        File fileToReturn = new File(path + filename);
        if(fileToReturn.exists()) {
            return Optional.of(new UrlResource( fileToReturn.toURI()));
        } else {
            return Optional.empty();
        }
    }
}
