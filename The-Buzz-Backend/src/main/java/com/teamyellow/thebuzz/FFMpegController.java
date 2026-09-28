package com.teamyellow.thebuzz;

import com.teamyellow.thebuzz.Resources.ResourcePaths;
import com.teamyellow.thebuzz.Services.LocalStorage;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

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

    public boolean splitAudioIntoSegments(String fileInput, int lengthOfSegments) throws IOException {
        FFmpegProbeResult probeResult = fprobe.probe(fileInput);

        if(!probeResult.hasError()) {
            long totalSegments = (long) Math.ceil(probeResult.getFormat().getDuration() / lengthOfSegments);

            for(int i = 0; i < totalSegments; i++) {
                splitAudio(probeResult, lengthOfSegments, i, "segment" + i + ".mp3");
            }
        } else {
            throw new FileNotFoundException();
        }
        return true;
    }

    private boolean splitAudio(FFmpegProbeResult probeResult, int lengthOfSegments, int offset, String fileOutput) throws IOException {
        File file = new File(fileOutput);
        if(!file.exists()) {
            LocalStorage.createEmptyFile(fileOutput);
        }

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(probeResult)
                .done()

                .addOutput(ResourcePaths.TEMP_DIRECTORY + "temp")
                .setDuration(lengthOfSegments, TimeUnit.SECONDS)
                .setStartOffset(offset, TimeUnit.SECONDS)
                .setAudioCodec("libmp3lame")
                .setFilename(fileOutput)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, fprobe);
        executor.createJob(builder).run();

        return false;
    }
}
