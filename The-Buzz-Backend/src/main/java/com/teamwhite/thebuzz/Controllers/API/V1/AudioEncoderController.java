/**
 * Audio Encoder Endpoint
 */

package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Records.ContentTypes;
import com.teamwhite.thebuzz.Resources.ResourcePaths;
import com.teamwhite.thebuzz.Services.AudioProcessingService;
import com.teamwhite.thebuzz.Services.FFMpegController;
import com.teamwhite.thebuzz.Services.LocalStorage;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/upload")
    // @TODO SPLIT INTO TWO SEPARATE ENDPOINTS
    @PreAuthorize("hasAnyAuthority('CAN_UPLOAD_LIVE_STREAM_SEGMENTS', 'CAN_UPLOAD_ANY_PRERECORDED_SHOW_AUDIO')")
    public ResponseEntity<String> uploadRecordedAudio
            (@RequestParam("file") MultipartFile file,
             @RequestParam(required = false) String resourcePath,
             @RequestParam("isLive") boolean isLive,
             HttpExchange.Principal principal) throws IOException {
        // Adds the newly created file into the queue to create the playlist with
        FFMpegController ffMpegController = new FFMpegController();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(String.valueOf(file.getBytes().length));
        String createdFileName;
        // @TODO DATA VALIDATION
        // Call will log the userId UUID in string format
        Logger.getGlobal().info(SecurityContextHolder.getContext().getAuthentication().getDetails().toString());

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
