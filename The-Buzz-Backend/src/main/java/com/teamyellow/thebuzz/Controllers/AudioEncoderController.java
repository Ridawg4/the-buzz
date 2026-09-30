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
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/audio")
public class AudioEncoderController {
    private static final AtomicBoolean killSig = new AtomicBoolean(false);
    private static String[] lastThreeLiveSegmentFiles =  {"", "", ""};

    static {
        PlaylistBuilder.init(killSig);
    }

    @GetMapping("/live")
    public ResponseEntity<Resource> getAudio() throws MalformedURLException {
        ResponseEntity<Resource> resp;

        /* An Optional<T> is a wrapper you can create to alert the programmer that an object
          or may not be present inside. This is a slightly nicer way to handle null objects */
        Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                ResourcePaths.LIVE_SEGMENTS_DIRECTORY, "live.m3u8");

        if(resourceOptional.isPresent()) {
            resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
            // HTTP standard indicate that .m3u8 files are returned with the type shown below
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/x-mpegURL");

        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return resp;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getLiveAudioSegment(@PathVariable String filename) throws MalformedURLException {
        ResponseEntity<Resource> resp;

        Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                ResourcePaths.LIVE_SEGMENTS_DIRECTORY, filename);

        if(resourceOptional.isPresent()) {
            resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return resp;
    }

    @GetMapping("/{resource}/{filename}")
    public ResponseEntity<Resource> getAudio(@PathVariable String filename, @PathVariable String resource) throws MalformedURLException {
        ResponseEntity<Resource> resp;

        Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                ResourcePaths.TEMP_DIRECTORY + resource + ResourcePaths.SEPARATOR
                        + "Segments", filename);

        if(resourceOptional.isPresent()) {
            resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
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
            lastThreeLiveSegmentFiles =
                    AudioProcessingService.processLiveAudioFiles(ffMpegController, file, lastThreeLiveSegmentFiles);
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(Arrays.toString(lastThreeLiveSegmentFiles));

        } else {
            AudioProcessingService.processPrerecordedAudioFiles(ffMpegController, recordingName, file);
        }

        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.ACCEPTED);

        return resp;
    }

    // @TODO DOCUMENT AND MAKE PRETTY
}
