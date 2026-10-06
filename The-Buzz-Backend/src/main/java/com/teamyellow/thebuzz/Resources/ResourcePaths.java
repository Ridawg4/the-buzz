package com.teamyellow.thebuzz.Resources;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public class ResourcePaths {
    /** The system file separator character
     * Since this differs between operating systems, it's best to use this separator
     * for file navigation */
    public static final String SEPARATOR = File.separator;

    /** The global directory that the executable is operating inside
     * Ends with a file separator */
    private static final String WORKING_DIRECTORY = System.getenv("PWD") + SEPARATOR;

    /** The global directory where all created folders and files live
     * Ends with a file separator */
    public static final String APPLICATION_DIRECTORY = WORKING_DIRECTORY + SEPARATOR + "The-Buzz" + SEPARATOR;

    /** The local installation to FFMpeg */
    public static final String FFMPEG_INSTALL = "/opt/homebrew/bin/ffmpeg";
    /** The local installation to FProbe */
    public static final String FPROBE_INSTALL = "/opt/homebrew/bin/ffprobe";

    /** The reference to the Temp directory inside the local file system
     * Ends with a file separator */
    public static final String TEMP_DIRECTORY = APPLICATION_DIRECTORY + "Temp" + SEPARATOR;

    /** A reference to the Live directory inside the local file system
     * Ends with a file separator */
    public static final String LIVE_DIRECTORY = APPLICATION_DIRECTORY + "Live" + SEPARATOR;

    /** A reference to the Live Segments directory inside the local file system
     * Ends with a file separator */
    public static final String LIVE_SEGMENTS_DIRECTORY = APPLICATION_DIRECTORY + "Live" + SEPARATOR + "Segments" + SEPARATOR;
}
