package com.teamyellow.thebuzz.Controllers;

import com.teamyellow.thebuzz.Records.Audio;
import com.teamyellow.thebuzz.Services.LocalStorage;
import com.teamyellow.thebuzz.Services.M3U8Encoder;
import com.teamyellow.thebuzz.Services.PlaylistBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/audio")
public class AudioEncoderController {
    private static final AtomicBoolean killSig = new AtomicBoolean(false);
    static {
        PlaylistBuilder.init(killSig);
    }

    @PostMapping("/live")
    public ResponseEntity<String> uploadAudioClip(@RequestParam("file") MultipartFile file) {
        String fileName = LocalStorage.addToTempStorage(file, file.getOriginalFilename());

        PlaylistBuilder.addFileToQueue(fileName);
        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.ACCEPTED);

        return resp;
    }

    @GetMapping("/live")
    public ResponseEntity<Resource> getAudio() throws MalformedURLException {
        final String LOCAL_DIRECTORY = System.getenv("PWD");
        URI filePath = Path.of(LOCAL_DIRECTORY + File.separator
                + "Live" + File.separator + "Live.m3u8").toUri();
        ResponseEntity<Resource> resp;

        if(new File(filePath).exists()) {
            Resource file = new UrlResource(filePath);
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(file.getFilename());

            resp = new ResponseEntity<>(file, HttpStatus.OK);
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/x-mpegURL");
        } else {
            resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return resp;
    }

    @GetMapping("/live/{file}")
    public ResponseEntity<Resource> getLiveAudioSegment(@PathVariable String path) throws MalformedURLException {
        final String LOCAL_DIRECTORY = System.getenv("PWD");
        URI filePath = Path.of(LOCAL_DIRECTORY + File.separator
                + "TEMP" + File.separator + path).toUri();

        Resource file = new UrlResource(filePath);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(file.getFilename());

        ResponseEntity<Resource> resp = new ResponseEntity<>(file, HttpStatus.OK);

        return resp;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getAudio(@PathVariable String filename) throws MalformedURLException {
        final String LOCAL_DIRECTORY = System.getenv("PWD");
        URI filePath = Path.of(LOCAL_DIRECTORY + File.separator
                + "TEMP" + File.separator + filename).toUri();

        Resource file = new UrlResource(filePath);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(file.getFilename());

        ResponseEntity<Resource> resp = new ResponseEntity<>(file, HttpStatus.OK);


        return resp;
    }
}
