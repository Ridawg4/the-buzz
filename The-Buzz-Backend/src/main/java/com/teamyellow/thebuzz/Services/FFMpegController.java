package com.teamyellow.thebuzz.Services;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import com.teamyellow.thebuzz.Services.LocalStorage;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FFMpegController {
    private static FFmpeg ffmpeg = null;
    private static FFprobe fprobe = null;
    private static long currentFileIndex = 0;

    public FFMpegController() {
        try {
            ffmpeg = new FFmpeg(ResourcePaths.FFMPEG_INSTALL);
            fprobe = new FFprobe(ResourcePaths.FPROBE_INSTALL);
        } catch (IOException e) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("FFMPEG or FPROBE not found at listed directory." +
                    " All FFMPEG and FPROBE methods will result in an error.");
        }
    }

    public String[] splitAudioIntoSegments(String fileInput, int lengthOfSegments, String fileOutput,
                                           boolean isLive) throws IOException {
        FFmpegProbeResult probeResult = fprobe.probe(fileInput);
        String[] lastThreeFileNames = new String[3];

        if(!probeResult.hasError()) {
            long totalSegments = (long) Math.ceil(probeResult.getFormat().getDuration() / lengthOfSegments);

            if (isLive) {
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

                for (int i = 0; i < totalSegments; i++) {
                    if (totalSegments - i == 3) {
                        lastThreeFileNames[0] = String.valueOf(currentFileIndex) + ".mp3";
                        lastThreeFileNames[1] = String.valueOf(currentFileIndex + 1) + ".mp3";
                        lastThreeFileNames[2] = String.valueOf(currentFileIndex + 2) + ".mp3";
                    }
                    splitAudio(probeResult, lengthOfSegments, i, currentFileIndex + ".mp3", fileOutput);
                    currentFileIndex++;
                }
            } else {
                for (int i = 0; i < totalSegments; i++) {
                    splitAudio(probeResult, lengthOfSegments, i, i + ".mp3", fileOutput);
                    currentFileIndex++;
                }
            }
        } else {
            throw new FileNotFoundException();
        }
        return lastThreeFileNames;
    }

    private boolean splitAudio(FFmpegProbeResult probeResult, int lengthOfSegments, int offset, String fileName, String fileOutput) throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(probeResult)
                .done()

                .addOutput(fileOutput + fileName)
                .setDuration((long) (lengthOfSegments + 0.05), TimeUnit.SECONDS)
                .setStartOffset((long) (offset * lengthOfSegments), TimeUnit.SECONDS)
                .setAudioCodec("libmp3lame")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, fprobe);
        executor.createJob(builder).run();

        return false;
    }
}
