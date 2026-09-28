package com.teamyellow.thebuzz.Services;

import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class LocalStorage {
    private static final String LOCAL_DIRECTORY = System.getenv("PWD");

    public static boolean addToTempStorage(MultipartFile file) {
        File tempFile = new File(Path.of(LOCAL_DIRECTORY + File.separator
                + "TEMP" + File.separator + file.getName() + ".mp3").toUri());
        try {
            tempFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(tempFile);

            stream.write(file.getBytes());
            stream.close();
        } catch (IOException e) {

        }

        return true;
    }
}
