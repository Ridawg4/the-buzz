/**
 * Audio Encoder Endpoint
 */

package com.teamyellow.thebuzz.Controllers;

import com.teamyellow.thebuzz.Records.ContentTypes;
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
import java.util.concurrent.Executors;
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
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, ContentTypes.M3U8);

        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Return the generated request
        return resp;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getLiveAudioSegment(@PathVariable String filename) throws MalformedURLException {
        ResponseEntity<Resource> resp;

        // Creates the Optional<T> received from local storage
        Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                ResourcePaths.LIVE_SEGMENTS_DIRECTORY, filename);

        if(resourceOptional.isPresent()) {
            // If the Optional<T> is not empty, send the file contained inside
            resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, ContentTypes.MP3);

        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Return the generated request
        return resp;
    }

    @GetMapping("/{resource}/{filename}")
    public ResponseEntity<Resource> getAudio(@PathVariable String filename, @PathVariable String resource) throws MalformedURLException {
        ResponseEntity<Resource> resp;

        // Creates the Optional<T> received from local storage
        Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                ResourcePaths.TEMP_DIRECTORY + resource + ResourcePaths.SEPARATOR
                        + "Segments" + ResourcePaths.SEPARATOR, filename);

        if(resourceOptional.isPresent()) {
            // If the Optional<T> is not empty, send those files
            resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
            // Required by HTTP HLS standard, clients deny loading the files without this tag
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, ContentTypes.M3U8);
        } else {
            // If file is not present, alert client of such
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

        // @TODO Abstract this method call to either a SpringBoot job handler
        // or implement a job handler class
        /* For long running tasks, it is necessary to create a new Thread to run the tasks
           Otherwise, further code will not be sent until the tasks are completed,
           which can cause users to believe something is broken

           Creating a new Thread solves this problem, as it allows the current thread to
           finish execution and the new Thread will run the tasks separately as called.
         */
        Executors.defaultThreadFactory().newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Processing live stream audio clips is separate than prerecorded audio clips
                    if (isLive) {
                        /* Preserves the last three files generated to allow
                     new listeners to get those files and for current listeners to know
                     where to start */
                        lastThreeLiveSegmentFiles =
                                AudioProcessingService.processLiveAudioFiles(ffMpegController,
                                        file, lastThreeLiveSegmentFiles);
                    } else {
                        // Doesn't need to preserve any files upon processing
                        AudioProcessingService.processPrerecordedAudioFiles(ffMpegController,
                                recordingName, file);
                    }
                } catch (IOException e) {
                    // In the case the thread encounters an error
                    throw new RuntimeException(e.getMessage());
                }
            }
        }).start(); // Starts this thread

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
