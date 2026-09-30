package com.teamyellow.thebuzz.Services;

import com.teamyellow.thebuzz.Exceptions.FilePathEmptyException;
import com.teamyellow.thebuzz.Exceptions.NoFilesToEncodeInPlaylist;
import com.teamyellow.thebuzz.Resources.ResourcePaths;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import org.apache.commons.io.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class PlaylistBuilder {
    private static final Queue<String> uriToFiles = new ConcurrentLinkedQueue<>();
    private static final AtomicLong indexOfPlaylist = new AtomicLong(0);
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Creates a Playlist file based on contents of a filepath
     * @param filePath Path to look for files to put into Playlist
     * @param fileName Name for the Playlist file
     * @param isLive Is the file created based for the Live playlist?
     */
    public static void buildQueueFromFolder(String filePath, String fileName, boolean isLive) throws FileNotFoundException, FilePathEmptyException {
        File directory = new File(filePath);
        // Since we don't know the amount of .mp3 files in the directory before, we use an ArrayList
        // for its resizing capabilities
        ArrayList<String> fileNamesAl = new ArrayList<>();

        if(directory.exists()) {
            if(directory.listFiles() == null || !(Objects.requireNonNull(directory.listFiles()).length > 0)) {
                throw new FilePathEmptyException("The directory at: " + filePath + " was empty");
            }

            for(File file : Objects.requireNonNull(directory.listFiles())) {
                if(file.getName().contains(".mp3")) {
                    fileNamesAl.add(file.getName());
                }
            }
        } else {
            throw new FileNotFoundException("No existing file at " + filePath);
        }

        // Since the files are in an ArrayList, they are not sorted. We need the files in order to
        // properly create the Playlist file, so we sort them.
        fileNamesAl.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(Integer.parseInt(o1.substring(0, o1.length() - ".mp3".length()))
                        < Integer.parseInt(o2.substring(0, o2.length() - ".mp3".length()))) {
                    return -1;
                }
                if(Integer.parseInt(o1.substring(0, o1.length() - ".mp3".length()))
                        > Integer.parseInt(o2.substring(0, o2.length() - ".mp3".length()))) {
                    return 1;
                }
                return 0;
            }
        });

        // Now that our files are sorted, we can put them in an array in order
        String[] uriNames = fileNamesAl.toArray(new String[0]);
        generateM3U8(uriNames, filePath, fileName, isLive);

    }

    private static void generateM3U8(String[] uriToFiles, String filePath, String filename, boolean isLive) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlaylist playlist;

                // If the Playlist is live, we create the playlist slightly differently
                try {
                    if (isLive) {
                        playlist = M3U8Encoder.createPlaylist(uriToFiles, uriToFiles.length, true);
                        indexOfPlaylist.getAndIncrement();
                    } else {
                        playlist = M3U8Encoder.createPlaylist(uriToFiles, 0, false);
                    }
                } catch (NoFilesToEncodeInPlaylist noFiles) {
                    throw new RuntimeException(noFiles.getMessage());
                }

                LocalStorage.addToAudioStorage(playlist, filename, filePath);
            }
        });
        thread.start();
    }
}
