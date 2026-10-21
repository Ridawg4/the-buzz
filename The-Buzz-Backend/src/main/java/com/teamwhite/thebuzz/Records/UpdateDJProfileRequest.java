package com.teamwhite.thebuzz.Records;

/**
 * Information a DJ can change on their profile.
 */
public record UpdateDJProfileRequest(
        String djName,
        String genre,
        String djPfpLocation
) {
}