/**
 * Audio Encoder Endpoint
 */

package com.teamyellow.thebuzz.Controllers;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import com.teamyellow.thebuzz.Services.AudioProcessingService;
import com.teamyellow.thebuzz.Services.FFMpegController;
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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadRecordedAudio
            (@RequestParam("file") MultipartFile file,
             @RequestParam("recordingName") String recordingName,
             @RequestParam("isLive") boolean isLive) throws IOException {
        // Adds the newly created file into the queue to create the playlist with
        FFMpegController ffMpegController = new FFMpegController();

        if(isLive) {
            AudioProcessingService.processLiveAudioFiles(ffMpegController, file);
        } else {
            AudioProcessingService.processPrerecordedAudioFiles(ffMpegController, recordingName, file);
        }

        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.ACCEPTED);

        return resp;
    }

    // @TODO DOCUMENT AND MAKE PRETTY
}
