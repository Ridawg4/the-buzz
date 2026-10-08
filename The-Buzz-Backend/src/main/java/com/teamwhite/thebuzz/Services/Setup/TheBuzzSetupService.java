package com.teamwhite.thebuzz.Services.Setup;

import com.teamwhite.thebuzz.Exceptions.SetupFailureException;
import com.teamwhite.thebuzz.Resources.ResourcePaths;

import java.io.File;

public class TheBuzzSetupService {

    /**
     * Sets up all the directories the application will expect to exist
     * If this fails, the application will fail to launch
     *
     * @throws SetupFailureException If the creation of the files fails due to an exception
     */
    public static void setupApplication() throws SetupFailureException{
        // Sets up a file reference to the directory listed | PWD/The-Buzz/
        File dir = new File(ResourcePaths.APPLICATION_DIRECTORY);

        try {
            // If the directory doesn't exist, create the directory
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (SecurityException securityException) {
            // If the directory cannot be created due to a security rule, throw a SetupFailure
            throw new SetupFailureException("Failed to create directory '/The-Buzz': "
                    + securityException.getMessage());
        }

        // Sets up a file reference to the directory listed | PWD/The-Buzz/Temp
        dir = new File(ResourcePaths.TEMP_DIRECTORY);

        try {
            // If the directory doesn't exist, create the directory
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (SecurityException securityException) {
            // If the directory cannot be created due to a security rule, throw a SetupFailure
            throw new SetupFailureException("Failed to create directory '/The-Buzz/Temp': "
                    + securityException.getMessage());
        }

        // Sets up a file reference to the directory listed | PWD/The-Buzz/Live
        dir = new File(ResourcePaths.LIVE_DIRECTORY);

        try {
            // If the directory doesn't exist, create the directory
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (SecurityException securityException) {
            // If the directory cannot be created due to a security rule, throw a SetupFailure
            throw new SetupFailureException("Failed to create directory '/The-Buzz/Live': "
                    + securityException.getMessage());
        }

        // Sets up a file reference to the directory listed | PWD/The-Buzz/Live/Segments
        dir = new File(ResourcePaths.LIVE_SEGMENTS_DIRECTORY);

        try {
            // If the directory doesn't exist, create the directory
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (SecurityException securityException) {
            // If the directory cannot be created due to a security rule, throw a SetupFailure
            throw new SetupFailureException("Failed to create directory '/The-Buzz/Live/Segments': "
                    + securityException.getMessage());
        }
    }
}
