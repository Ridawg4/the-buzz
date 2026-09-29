package com.teamyellow.thebuzz.Services;

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
import java.util.logging.Logger;

public class PlaylistBuilder {
    private static final Queue<String> uriToFiles = new ConcurrentLinkedQueue<>();
    private static AtomicBoolean killSig = null;
    private static int segments = 3;
    private static long indexOfPlaylist = 0;

    public static void init(AtomicBoolean killSignal) {
        killSig = killSignal;
        generateM3U8(segments);
    }

    public static void addFileToQueue(String fileName) {
        uriToFiles.add(fileName);
    }

    public static void buildQueueFromFolder(String filePath) {
        File directory = new File(ResourcePaths.TEMP_DIRECTORY + filePath);
        ArrayList<String> fileNamesAl = new ArrayList<>();

        if(directory.exists()) {
            for(File file : Objects.requireNonNull(directory.listFiles())) {
                if(file.getName().contains(".mp3")) {
                    fileNamesAl.add(file.getName());
                }
            }
        } else {

        }
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

        Queue<String> fileNames = new ConcurrentLinkedQueue<>(fileNamesAl);
        fileNames.add(filePath);
        generateM3U8(fileNames);

    }

    private static void generateM3U8(int totalSegments) {
        Thread thread = new Thread(new Runnable() {
            private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            @Override
            public void run() {
                while(!killSig.get()) {
//                    LOGGER.info("queue length: " + uriToFiles.size());

                    if(uriToFiles.size() == totalSegments) {
                        String[] fileNames = new String[totalSegments];
                        for(int i = 0; i < totalSegments; i++) {
                            fileNames[i] = uriToFiles.poll();
                        }
                        MediaPlaylist playlist = M3U8Encoder.createPlaylist(fileNames, indexOfPlaylist, totalSegments);

                        LocalStorage.addToLiveStorage(playlist, "Live.m3u8");
                        indexOfPlaylist++;
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    private static void generateM3U8(Queue<String> uriToFiles) {
        Thread thread = new Thread(new Runnable() {
            private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

            @Override
            public void run() {
//                LOGGER.info("queue length: " + uriToFiles.size());
                int uriLength = uriToFiles.size();
                String[] fileNames = new String[uriLength];
                for (int i = 0; i < uriLength - 1; i++) {
                    fileNames[i] = uriToFiles.poll();
                }
                MediaPlaylist playlist = M3U8Encoder.createPlaylist(fileNames, 0, fileNames.length - 1);

                LocalStorage.addToStorage(playlist, "recorded.m3u8", "Temp" + ResourcePaths.SEPARATOR + uriToFiles.poll());
            }
        });
        thread.start();
    }
}
