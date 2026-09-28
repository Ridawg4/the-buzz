package com.teamyellow.thebuzz.Services;

import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.PartialSegment;
import io.lindstrom.m3u8.model.PlaylistType;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public class M3U8Encoder {

    public static MediaPlaylist createPlaylist(URI[] uris) {
        final String LOCAL_DIRECTORY = System.getenv("PWD");
        PartialSegment[] segments = new PartialSegment[3];

        for(int i = 0; i < uris.length; i++) {
            segments[i] = PartialSegment.builder()
                    .duration(10.0)
                    .uri(String.valueOf(uris[i]))
                    .build();
        }

        MediaPlaylist.Builder playlist = MediaPlaylist.builder();
        for(PartialSegment segment : segments) {
            playlist.addPartialSegments(segment);
        }
        playlist.allowCache(false);
        playlist.ongoing(true);

        return playlist.build();
    }
}
