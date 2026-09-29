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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api/v1/audio")
public class AudioEncoderController {
    private static final AtomicBoolean killSig = new AtomicBoolean(false);

    static {
        PlaylistBuilder.init(killSig);
    }


    @PostMapping("/live")
    public ResponseEntity<String> uploadLiveAudioClip(@RequestParam("file") MultipartFile file) throws IOException {
            // Takes the received file and creates a file for it in local storage
            String fileName = LocalStorage.addToTempStorage(file, file.getOriginalFilename());

            // Adds the newly created file into the queue to create the playlist with
            FFMpegController ffMpegController = new FFMpegController();

            ffMpegController.splitAudioIntoSegments(ResourcePaths.LIVE_DIRECTORY + fileName, 10,
                    ResourcePaths.LIVE_SEGMENTS_DIRECTORY + fileName);

            PlaylistBuilder.buildQueueFromFolder(ResourcePaths.LIVE_DIRECTORY +
                    ResourcePaths.SEPARATOR + "Segments" + ResourcePaths.SEPARATOR);
            ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.ACCEPTED);

            return resp;
        }

    @GetMapping("/live")
    public ResponseEntity<Resource> getAudio() throws MalformedURLException {
        ResponseEntity<Resource> resp;

        if(new File(ResourcePaths.LIVE_DIRECTORY + "live.m3u8").exists()) {
            Resource file = new UrlResource("file://" + ResourcePaths.LIVE_DIRECTORY + "live.m3u8");

            resp = new ResponseEntity<>(file, HttpStatus.OK);
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/x-mpegURL");
        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return resp;
    }

    @GetMapping("/live/{path}")
    public ResponseEntity<Resource> getLiveAudioSegment(@PathVariable String path) throws MalformedURLException {
        Resource file = new UrlResource(ResourcePaths.LIVE_SEGMENTS_DIRECTORY + path);
        ResponseEntity<Resource> resp;

        if(new File("file://" + ResourcePaths.LIVE_SEGMENTS_DIRECTORY + path).exists()) {
            resp = new ResponseEntity<>(file, HttpStatus.OK);
        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return resp;
    }

    @GetMapping("/{resource}/{filename}")
    public ResponseEntity<Resource> getAudio(@PathVariable String filename, @PathVariable String resource) throws MalformedURLException {
        ResponseEntity<Resource> resp;


        if(new File(ResourcePaths.TEMP_DIRECTORY + resource + ResourcePaths.SEPARATOR
                + "Segments" + ResourcePaths.SEPARATOR + filename).exists()) {
            Resource file = new UrlResource("file://" + ResourcePaths.TEMP_DIRECTORY + resource
                    + ResourcePaths.SEPARATOR
                    + "Segments" + ResourcePaths.SEPARATOR + filename);
            resp = new ResponseEntity<>(file, HttpStatus.OK);
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/x-mpegURL");
        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return resp;
    }

    @PostMapping("/recorded")
    public ResponseEntity<String> uploadRecordedAudio(@RequestParam("file") MultipartFile file,
                                                      @RequestParam String recordingName) throws IOException {
        // Takes the received file and creates a file for it in local storage
        String fileName = LocalStorage.addToTempStorage(file, file.getOriginalFilename(), recordingName);

        // Adds the newly created file into the queue to create the playlist with
        FFMpegController ffMpegController = new FFMpegController();

        ffMpegController.splitAudioIntoSegments(ResourcePaths.TEMP_DIRECTORY + recordingName
                + ResourcePaths.SEPARATOR + fileName, 10,
                ResourcePaths.TEMP_DIRECTORY + recordingName
                        + ResourcePaths.SEPARATOR + "Segments");

        PlaylistBuilder.buildQueueFromFolder(recordingName
                + ResourcePaths.SEPARATOR + "Segments" + ResourcePaths.SEPARATOR);
        LocalStorage.removeFileFromLocalStorage(fileName, ResourcePaths.TEMP_DIRECTORY + recordingName);
        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.ACCEPTED);

        return resp;
    }
}
