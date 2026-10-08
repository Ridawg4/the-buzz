package com.teamwhite.thebuzz.Services.Audio;

import com.teamwhite.thebuzz.Resources.ResourcePaths;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FFMpegController {
    private static FFmpeg ffmpeg = null;
    private static FFprobe fprobe = null;
    private static long currentFileIndex = 0;

    public FFMpegController() {
        try {
            // Attempts to locate the FFMpeg and FFProbe scripts
            ffmpeg = new FFmpeg(ResourcePaths.FFMPEG_INSTALL);
            fprobe = new FFprobe(ResourcePaths.FPROBE_INSTALL);
        } catch (IOException e) {
            // If they are not present, the requests needing these features will fail
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("FFMPEG or FPROBE not found at listed directory." +
                    " All FFMPEG and FPROBE methods will result in an error.");
        }
    }

    /**
     * Splits audio supplied from the input into separate files in the output directory
     * @param fileInput The file to split into parts
     * @param desiredSegmentSize The length of the desired segment length
     * @param fileOutput Directory to place the split audio files within
     * @param isLive Is the files received to be used for the live feed?
     * @return String[] The last three created files, or all not enough segments exist to create 3 files
     * @throws IOException If the process fails
     */
    public String[] splitAudioIntoSegments(String fileInput, int desiredSegmentSize, String fileOutput,
                                           boolean isLive) throws IOException {
        FFmpegProbeResult probeResult = fprobe.probe(fileInput);
        String[] lastThreeFileNames = new String[3];

        // Checks whether the probeResult contains no errors
        if(!probeResult.hasError()) {
            // Gets the maximum amount of segments that can be created
            // Rounded to include last section of the clip that is less than 10 seconds
            long totalSegments = (long) Math.ceil(probeResult.getFormat().getDuration() / desiredSegmentSize);

            // Checks if the files is live
            if (isLive) {
                // Check if the totalSegments are less than 1, 2, or 3, and set the exclusions
                if (totalSegments == 1) {
                    lastThreeFileNames[0] = String.valueOf(currentFileIndex) + ".mp3";
                } else if (totalSegments == 2) {
                    lastThreeFileNames[0] = String.valueOf(currentFileIndex) + ".mp3";
                    lastThreeFileNames[1] = String.valueOf(currentFileIndex + 1) + ".mp3";
                } else if (totalSegments == 3) {
                    lastThreeFileNames[0] = String.valueOf(currentFileIndex) + ".mp3";
                    lastThreeFileNames[1] = String.valueOf(currentFileIndex + 1) + ".mp3";
                    lastThreeFileNames[2] = String.valueOf(currentFileIndex + 2) + ".mp3";
                }

                // Increment through each segment, split the audio
                for (int i = 0; i < totalSegments; i++) {
                    // If the totalSegments - index = 3, then we can assume the two will be the last files
                    // So we can add the current generated filename, and the next two generated names
                    if (totalSegments - i == 3) {
                        lastThreeFileNames[0] = String.valueOf(currentFileIndex) + ".mp3";
                        lastThreeFileNames[1] = String.valueOf(currentFileIndex + 1) + ".mp3";
                        lastThreeFileNames[2] = String.valueOf(currentFileIndex + 2) + ".mp3";
                    }
                    // We use a fileIndex counter, or else the browser would see the Playlist file
                    // with files that the browser has already cached and disregard pulling them,
                    // even if they are updated
                    splitAudio(probeResult, desiredSegmentSize, i, currentFileIndex + ".mp3", fileOutput);
                    currentFileIndex++;
                }
            }
            // If the file is not live, use the index as the name
            else {
                // Increment through each segment, split the audio
                for (int i = 0; i < totalSegments; i++) {
                    // Here, since the browser will not get updating segments, we can use the index
                    // directly as the filename
                    splitAudio(probeResult, desiredSegmentSize, i, i + ".mp3", fileOutput);
                    //currentFileIndex++;
                }
            }
        }
        // If the probeResult for the file that will be split contains an error
        else {
            throw new FileNotFoundException();
        }
        // After splitting the files, return the lastThreeFileNames generated
        // If the file is prerecorded, this value will be ignored
        return lastThreeFileNames;
    }

    /**
     * Splits the audio files based on the filename, output path, desired size, and the files offset
     * @param probeResult Result of the probe
     * @param desiredSegmentSize The size to split the files into
     * @param offset The offset to the file, needed for splitting the audio on one file
     * @param fileName The filename for the newly created file
     * @param fileOutput The directory to place the newly created file
     * @throws IOException If the builder fails
     */
    private void splitAudio(FFmpegProbeResult probeResult, int desiredSegmentSize, int offset, String fileName, String fileOutput) throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(probeResult)
                .done()

                .addOutput(fileOutput + fileName)
                // An attempt to fix very short skipping in browser, doesn't fix the issue
                .setDuration(desiredSegmentSize, TimeUnit.SECONDS)
                // Uses offset and desiredSegmentSize to determine when to start the clip
                .setStartOffset((long) (offset * desiredSegmentSize), TimeUnit.SECONDS)
                // The .mp3 Codec
                .setAudioCodec("libmp3lame")
                .done();

        // Creates the executor for the task
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, fprobe);
        executor.createJob(builder).run();
    }
}
