package com.teamyellow.thebuzz.Services;

import com.teamyellow.thebuzz.Exceptions.NoFilesToEncodeInPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;

import java.util.Arrays;

public class M3U8Encoder {
    public static MediaPlaylist createPlaylist(String[] fileNames,
                                               int totalSegments, boolean isLive) throws NoFilesToEncodeInPlaylist {
        // If there is 0 or less totalSegments, then there is nothing to generate from
        if(fileNames.length == 0) {
            throw new NoFilesToEncodeInPlaylist("Attempted to encode files into a Playlist, but was provided "
            + Arrays.toString(fileNames) + " as files");
        }

        MediaSegment[] segments = new MediaSegment[totalSegments];
        String firstFileInList = fileNames[0]; // First file needed to set the
        long indexOfFirstFile = Long.parseLong(firstFileInList.substring(0, (firstFileInList.length() - ".mp3".length())));

        // For each file, create a segment for it
        for(int i = 0; i < totalSegments; i++) {
            segments[i] = MediaSegment.builder()
                    .duration(10.0)
                    .uri(String.valueOf(fileNames[i]))
                    .build();
        }

        MediaPlaylist.Builder playlist = MediaPlaylist.builder();
        playlist.version(3);

        // Add all the segments to the playlist
        for(MediaSegment segment : segments) {
            playlist.addMediaSegments(segment);
        }

        if(isLive) {
            playlist.allowCache(false);
            playlist.ongoing(true);
            /* The playlist mediaSequence must be the index of the first file for live streams
             otherwise, the audio player binds to the end and starts there and won't update
             when new files are added */
            playlist.mediaSequence(indexOfFirstFile);
        } else {
            playlist.allowCache(true);
            playlist.ongoing(false);
            playlist.mediaSequence(0);
        }
        // Tells the browser that all segments should be 10 seconds long
        playlist.targetDuration(10);

        // Build the Playlist
        return playlist.build();
    }
}
