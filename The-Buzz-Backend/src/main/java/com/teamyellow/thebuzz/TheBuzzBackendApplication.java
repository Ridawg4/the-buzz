package com.teamyellow.thebuzz;

import com.teamyellow.thebuzz.Exceptions.SetupFailureException;
import com.teamyellow.thebuzz.Services.TheBuzzSetupController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class TheBuzzBackendApplication {

    public static void main(String[] args) {
        try {
            // Sets up the reqired files and directories for the application
            TheBuzzSetupController.setupApplication();
        } catch (SetupFailureException setupFailure) {
            // If the setup fails, throw a severe log message and exit the application
            Logger.getGlobal().severe(setupFailure.getMessage());
            System.exit(1);
        }

        // Initializes the SpringBoot application with the Main class
        SpringApplication application = new SpringApplication(TheBuzzBackendApplication.class);
        // Starts the Spring Boot application
        application.run(args);
    }

}
