package com.teamyellow.thebuzz.Services;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class AudioProcessingService {
    public static void processPrerecordedAudioFiles(FFMpegController ffMpegController, String recordingName, MultipartFile file) throws IOException {
        // Takes the received file and creates a file for it in local storage
        String fileName = LocalStorage.addToAudioStorage(file, file.getOriginalFilename(), ResourcePaths.TEMP_DIRECTORY, recordingName);

        ffMpegController.splitAudioIntoSegments(ResourcePaths.TEMP_DIRECTORY + recordingName
                        + ResourcePaths.SEPARATOR + fileName, 10,
                ResourcePaths.TEMP_DIRECTORY + recordingName
                        + ResourcePaths.SEPARATOR + "Segments");

        PlaylistBuilder.buildQueueFromFolder(ResourcePaths.TEMP_DIRECTORY + recordingName
                + ResourcePaths.SEPARATOR + "Segments" + ResourcePaths.SEPARATOR);
        LocalStorage.removeFileFromLocalStorage(fileName, ResourcePaths.TEMP_DIRECTORY + recordingName);
    }

    public static void processLiveAudioFiles(FFMpegController ffMpegController, MultipartFile file) throws IOException {
        // Takes the received file and creates a file for it in local storage
        String fileName = LocalStorage.addToAudioStorage(file, file.getOriginalFilename(),ResourcePaths.LIVE_DIRECTORY);

        ffMpegController.splitAudioIntoSegments(ResourcePaths.LIVE_DIRECTORY
                        + fileName, 10,
                ResourcePaths.LIVE_SEGMENTS_DIRECTORY);

        PlaylistBuilder.buildQueueFromFolder(ResourcePaths.LIVE_SEGMENTS_DIRECTORY);
        LocalStorage.removeFileFromLocalStorage(fileName, ResourcePaths.LIVE_DIRECTORY);
    }
}
