package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Resources.ResourcePaths;

import java.io.IOException;

public class AudioProcessingService {
    /**
     * Processes all prerecorded audio clips
     *
     * @param ffMpegController The FFMpeg handler that will split all .mp3 files into segments
     * @param resourcePath The name of the folder to place the files in
     * @param fileName The name of the file to split
     * @throws IOException If the processing fails
     */
    public static void processPrerecordedAudioFiles(FFMpegController ffMpegController, String resourcePath, String fileName) throws IOException {
        // Splits the audio files into separate files based on the length and the value in lengthOfSegments
        ffMpegController.splitAudioIntoSegments(ResourcePaths.TEMP_DIRECTORY + resourcePath
                        + ResourcePaths.SEPARATOR + fileName, 10,
                ResourcePaths.TEMP_DIRECTORY + resourcePath
                        + ResourcePaths.SEPARATOR + "Segments" + ResourcePaths.SEPARATOR, false);

        // Builds the Playlist from the files present in the denoted directory
        PlaylistBuilder.buildQueueFromFolder(ResourcePaths.TEMP_DIRECTORY + resourcePath
                + ResourcePaths.SEPARATOR + "Segments" + ResourcePaths.SEPARATOR, "recorded.m3u8", false);
        // Removes the file that was uploaded
        LocalStorage.removeFileFromLocalStorage(fileName, ResourcePaths.TEMP_DIRECTORY + resourcePath + ResourcePaths.SEPARATOR);
    }

    /**
     * Processes all live audio clips
     *
     * @param ffMpegController The FFMpeg handler that will split all .mp3 files into segments
     * @param fileName The name of the file to split
     * @param filesToExcludeFromDeletion The files to not delete inside the live file path
     * @return String[] The newest filenames not to delete - will contain max of 3 files
     * @throws IOException If the process fails
     */
    public static String[] processLiveAudioFiles(FFMpegController ffMpegController, String fileName, String[] filesToExcludeFromDeletion) throws IOException {
        // Clears the live clips in the directory except the ones denoted by filesToExcludeFromDeletion
        LocalStorage.removeFilesFromLocalStorage(ResourcePaths.LIVE_SEGMENTS_DIRECTORY, filesToExcludeFromDeletion);

        // Splits the audio files into separate files based on the length and the value in lengthOfSegments
        String[] lastThreeFiles = ffMpegController.splitAudioIntoSegments(ResourcePaths.LIVE_DIRECTORY
                        + fileName, 10,
                ResourcePaths.LIVE_SEGMENTS_DIRECTORY, true);

        // Builds the Playlist from the files present in the live directory
        PlaylistBuilder.buildQueueFromFolder(ResourcePaths.LIVE_SEGMENTS_DIRECTORY, "live.m3u8", true);
        // Removes the file that was uploaded
        LocalStorage.removeFileFromLocalStorage(fileName, ResourcePaths.LIVE_DIRECTORY);

        return lastThreeFiles;
    }
}
