package com.teamyellow.thebuzz.Resources;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public class ResourcePaths {
    // The system file separator character
    public static final String SEPARATOR = File.separator;

    // The global directory that the executable is operating inside
    public static final String WORKING_DIRECTORY = System.getenv("PWD") + SEPARATOR;

    // The reference to the Temp directory
    public static final String TEMP_DIRECTORY = WORKING_DIRECTORY + SEPARATOR + "Temp" + SEPARATOR;

    // A reference to the Live directory
    public static final String LIVE_DIRECTORY = WORKING_DIRECTORY + SEPARATOR + "Live" + SEPARATOR;
    // A reference to the Live Segments directory
    public static final String LIVE_SEGMENTS_DIRECTORY = WORKING_DIRECTORY + SEPARATOR + "Live" + SEPARATOR + "Segments" + SEPARATOR;
}
