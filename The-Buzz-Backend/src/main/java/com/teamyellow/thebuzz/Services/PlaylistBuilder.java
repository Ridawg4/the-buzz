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
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class PlaylistBuilder {
    private static final Queue<String> uriToFiles = new ConcurrentLinkedQueue<>();
    private static AtomicBoolean killSig = null;
    private static AtomicInteger segments = null;
    private static long indexOfPlaylist = 0;

    public static void init(AtomicBoolean killSignal) {
        killSig = killSignal;
        segments = new AtomicInteger(3);
        generateM3U8(segments);
    }

    public static void addFileToQueue(String fileName) {
        uriToFiles.add(fileName);
    }

    public static void addFilesToQueueFromFolder() {
        File directory = new File(ResourcePaths.TEMP_DIRECTORY);

        if(directory.exists()) {
            for(File file : Objects.requireNonNull(directory.listFiles())) {
                // This stops the M3U8 generator getting one file and processing it as no others exist
                segments.incrementAndGet();
                if(file.getName().contains(".mp3")) {
                    segments.incrementAndGet();
                    uriToFiles.add(file.getName());
                }
            }
        } else {

        }
        segments.decrementAndGet();
    }

    private static void generateM3U8(AtomicInteger totalSegments) {
        Thread thread = new Thread(new Runnable() {
            private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            @Override
            public void run() {
                while(!killSig.get()) {
                    LOGGER.info("queue length: " + uriToFiles.size());

                    if(uriToFiles.size() == totalSegments.intValue()) {
                        String[] fileNames = new String[totalSegments.intValue()];
                        for(int i = 0; i < totalSegments.intValue(); i++) {
                            fileNames[i] = uriToFiles.poll();
                        }
                        MediaPlaylist playlist = M3U8Encoder.createPlaylist(fileNames, indexOfPlaylist);

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
}
