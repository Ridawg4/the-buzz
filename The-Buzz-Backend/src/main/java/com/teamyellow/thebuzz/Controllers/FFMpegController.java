package com.teamyellow.thebuzz.Controllers;

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
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FFMpegController {
    private static FFmpeg ffmpeg = null;
    private static FFprobe fprobe = null;

    public FFMpegController() {
        try {
            ffmpeg = new FFmpeg(ResourcePaths.FFMPEG_INSTALL);
            fprobe = new FFprobe(ResourcePaths.FPROBE_INSTALL);
        } catch (IOException e) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("FFMPEG or FPROBE not found at listed directory." +
                    " All FFMPEG and FPROBE methods will result in an error.");
        }
    }

    public boolean splitAudioIntoSegments(String fileInput, int lengthOfSegments, String fileOutput) throws IOException {
        FFmpegProbeResult probeResult = fprobe.probe(fileInput);

        if(!probeResult.hasError()) {
            long totalSegments = (long) Math.ceil(probeResult.getFormat().getDuration() / lengthOfSegments);

            for(int i = 0; i < totalSegments; i++) {
                splitAudio(probeResult, lengthOfSegments, i, i + ".mp3", fileOutput);
            }
        } else {
            throw new FileNotFoundException();
        }
        return true;
    }

    private boolean splitAudio(FFmpegProbeResult probeResult, int lengthOfSegments, int offset, String fileName, String fileOutput) throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(probeResult)
                .done()

                .addOutput(fileOutput + ResourcePaths.SEPARATOR + fileName)
                .setDuration((long) (lengthOfSegments + 0.05), TimeUnit.SECONDS)
                .setStartOffset((long) (offset * lengthOfSegments), TimeUnit.SECONDS)
                .setAudioCodec("libmp3lame")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, fprobe);
        executor.createJob(builder).run();

        return false;
    }
}
