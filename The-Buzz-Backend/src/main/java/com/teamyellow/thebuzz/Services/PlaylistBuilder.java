package com.teamyellow.thebuzz.Services;

import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class PlaylistBuilder {
    private static final Queue<String> uriToFiles = new ConcurrentLinkedQueue<>();
    private static AtomicBoolean killSig = null;
    private static long indexOfPlaylist = 0;

    public static void init(AtomicBoolean killSignal) {
        killSig = killSignal;
        generateM3U8();
    }

    public static void addFileToQueue(String fileName) {
        uriToFiles.add(fileName);
    }

    private static void generateM3U8() {
        Thread thread = new Thread(new Runnable() {
            private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            @Override
            public void run() {
                while(!killSig.get()) {
                    LOGGER.info("queue length: " + uriToFiles.size());

                    if(uriToFiles.size() == 3) {
                        String[] fileNames = new String[3];
                        for(int i = 0; i < 3; i++) {
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
