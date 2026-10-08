/**
 * Audio Encoder Endpoint
 */

package com.teamwhite.thebuzz.Controllers.API.V1.Audio;

import com.teamwhite.thebuzz.Records.ContentTypes;
import com.teamwhite.thebuzz.Resources.ResourcePaths;
import com.teamwhite.thebuzz.Services.Audio.AudioProcessingService;
import com.teamwhite.thebuzz.Services.Audio.FFMpegController;
import com.teamwhite.thebuzz.Services.LocalStorage;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/audio")
public class AudioEncoderController {
    private static String[] lastThreeLiveSegmentFiles =  {"", "", ""};

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
             @RequestParam("resourcePath") String resourcePath,
             @RequestParam("isLive") boolean isLive) throws IOException {
        // Adds the newly created file into the queue to create the playlist with
        FFMpegController ffMpegController = new FFMpegController();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(String.valueOf(file.getBytes().length));
        String createdFileName;

        if(file.getBytes().length == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(isLive) {
            createdFileName = LocalStorage.addToAudioStorage(file, file.getOriginalFilename(), ResourcePaths.LIVE_DIRECTORY);
        } else {
            createdFileName = LocalStorage.addToAudioStorage(file, file.getOriginalFilename(), ResourcePaths.TEMP_DIRECTORY, resourcePath);
        }

        final String fileName = createdFileName;
        /* For long-running tasks, it is necessary to create a new Thread to run the tasks
           Otherwise, further code will not be sent until the tasks are completed,
           which can cause users to believe something is broken

           Creating a new Thread solves this problem, as it allows the current thread to
           finish execution and the new Thread will run the tasks separately as called.

           Be careful when using this technique for requests that have received data,
           make sure that data is saved elsewhere first, or else when the request finishes,
           the data will be removed */
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
                                        fileName, lastThreeLiveSegmentFiles);
                    } else {
                        // Doesn't need to preserve any files upon processing
                        AudioProcessingService.processPrerecordedAudioFiles(ffMpegController,
                                resourcePath, fileName);
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
