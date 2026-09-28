/**
 * Audio Encoder Endpoint
 */

package com.teamyellow.thebuzz.Controllers;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import com.teamyellow.thebuzz.Services.LocalStorage;
import com.teamyellow.thebuzz.Services.PlaylistBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/audio")
public class AudioEncoderController {
    private static final AtomicBoolean killSig = new AtomicBoolean(false);
    static {
        PlaylistBuilder.init(killSig);
    }


    @PostMapping("/live")
    public ResponseEntity<String> uploadLiveAudioClip(@RequestParam("file") MultipartFile file) {
        // Takes the received file and creates a file for it in local storage
        String fileName = LocalStorage.addToTempStorage(file, file.getOriginalFilename());

        // Adds the newly created file into the queue to create the playlist with
        PlaylistBuilder.addFileToQueue(fileName);
        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.ACCEPTED);

        return resp;
    }

    @GetMapping("/live")
    public ResponseEntity<Resource> getAudio() throws MalformedURLException {
        ResponseEntity<Resource> resp;

        if(new File(ResourcePaths.LIVE_DIRECTORY + "live.m3u8").exists()) {
            Resource file = new UrlResource(ResourcePaths.LIVE_DIRECTORY + "live.m3u8");

            resp = new ResponseEntity<>(file, HttpStatus.OK);
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/x-mpegURL");
        } else {
            resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return resp;
    }

    @GetMapping("/live/{path}")
    public ResponseEntity<Resource> getLiveAudioSegment(@PathVariable String path) throws MalformedURLException {
        Resource file = new UrlResource(ResourcePaths.TEMP_DIRECTORY + path);
        ResponseEntity<Resource> resp;

        if(new File(ResourcePaths.LIVE_DIRECTORY + path).exists()) {
            resp = new ResponseEntity<>(file, HttpStatus.OK);
        } else {
            resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return resp;
    }

    @GetMapping("/recorded/{filename}")
    public ResponseEntity<Resource> getAudio(@PathVariable String filename) throws MalformedURLException {
        Resource file = new UrlResource(ResourcePaths.TEMP_DIRECTORY + filename);
        ResponseEntity<Resource> resp;

        if(new File(ResourcePaths.TEMP_DIRECTORY + filename).exists()) {
            resp = new ResponseEntity<>(file, HttpStatus.OK);
        } else {
            resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return resp;
    }

    @PostMapping("/recorded")
    public ResponseEntity<String> uploadRecordedAudio(@RequestParam("file") MultipartFile file) {
        // Takes the received file and creates a file for it in local storage
        String fileName = LocalStorage.addToTempStorage(file, file.getOriginalFilename());

        // Adds the newly created file into the queue to create the playlist with
        PlaylistBuilder.addFileToQueue(fileName);
        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.ACCEPTED);

        return resp;
    }
}
