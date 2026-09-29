package com.teamyellow.thebuzz.Services;

import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;

public class M3U8Encoder {

    public static MediaPlaylist createPlaylist(String[] fileNames, long playlistIndex) {
        MediaSegment[] segments = new MediaSegment[3];

        for(int i = 0; i < fileNames.length; i++) {
            segments[i] = MediaSegment.builder()
                    .duration(10.0)
                    .uri(String.valueOf(fileNames[i]))
                    .build();
        }

        MediaPlaylist.Builder playlist = MediaPlaylist.builder();
        for(MediaSegment segment : segments) {
            playlist.addMediaSegments(segment);
        }
        playlist.version(3);
        playlist.allowCache(false);
        playlist.ongoing(true);
        playlist.targetDuration(10);
        playlist.mediaSequence(playlistIndex);

        return playlist.build();
    }

    public static MediaPlaylist createPlaylist(String[] fileNames, long playlistIndex, int totalSegments) {
        MediaSegment[] segments = new MediaSegment[totalSegments];

        for(int i = 0; i < totalSegments; i++) {
            segments[i] = MediaSegment.builder()
                    .duration(10.0)
                    .uri(String.valueOf(fileNames[i]))
                    .build();
        }

        MediaPlaylist.Builder playlist = MediaPlaylist.builder();
        for(MediaSegment segment : segments) {
            playlist.addMediaSegments(segment);
        }
        playlist.version(3);
        playlist.allowCache(true);
        playlist.ongoing(false);
        playlist.targetDuration(10);
        playlist.mediaSequence(playlistIndex);

        return playlist.build();
    }
}
