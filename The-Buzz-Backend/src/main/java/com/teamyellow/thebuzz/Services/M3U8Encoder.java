package com.teamyellow.thebuzz.Services;

import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;

public class M3U8Encoder {
    public static MediaPlaylist createPlaylist(String[] fileNames, long playlistIndex,
                                               int totalSegments, boolean isLive) {
        if(totalSegments <=0) {
            throw new RuntimeException();
        }
        MediaSegment[] segments = new MediaSegment[totalSegments];
        String firstFileInList = fileNames[0];
        long indexOfFirstFile = Long.parseLong(firstFileInList.substring(0, (firstFileInList.length() - ".mp3".length())));

        for(int i = 0; i < totalSegments; i++) {
            segments[i] = MediaSegment.builder()
                    .duration(10.0)
                    .uri(String.valueOf(fileNames[i]))
                    .build();
        }

        MediaPlaylist.Builder playlist = MediaPlaylist.builder();
        playlist.version(3);

        for(MediaSegment segment : segments) {
            playlist.addMediaSegments(segment);
        }

        if(isLive) {
            playlist.allowCache(false);
            playlist.ongoing(true);
            playlist.mediaSequence(indexOfFirstFile);
        } else {
            playlist.allowCache(true);
            playlist.ongoing(false);
            playlist.mediaSequence(0);
        }
        playlist.targetDuration(10);

        return playlist.build();
    }
}
