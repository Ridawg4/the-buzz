
package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.DjRepository;
import com.teamwhite.thebuzz.model.DJ;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Service for handling DJ profile updates
 * and retrieving DJ profiles.
 */
@Service
public class DjService {

    private final DjRepository djRepository;

    // Constructor connects this service to the repository
    public DjService(DjRepository djRepository) {
        this.djRepository = djRepository;
    }

    /**
     * Updates a DJ's profile information.
     *
     * @param userId ID of the authenticated user
     * @param djName New DJ name
     * @param genre New music genre
     * @param pfpLocation New profile picture location
     * @return true if exactly one profile was updated
     */
    public boolean updateDJProfile(
            UUID userId,
            String djName,
            String genre,
            String pfpLocation) {

        // Make sure the user ID is provided
        if (userId == null) {
            throw new IllegalArgumentException(
                "User ID cannot be null"
            );
        }

        // Check that the DJ name is valid
        if (djName == null || djName.isBlank()
                || djName.length() > 255) {

            throw new IllegalArgumentException(
                "DJ name must be between 1 and 255 characters"
            );
        }

        // Check the genre length
        if (genre != null && genre.length() > 255) {

            throw new IllegalArgumentException(
                "Genre cannot exceed 255 characters"
            );
        }

        // Check the profile picture location length
        if (pfpLocation != null && pfpLocation.length() > 255) {

            throw new IllegalArgumentException(
                "Profile picture location is too long"
            );
        }

        // Call the repository to update MySQL
        int updatedRows = djRepository.updateDJProfile(
            userId.toString(),
            djName,
            genre,
            pfpLocation
        );

        // Return true if exactly one DJ profile was updated
        return updatedRows == 1;
    }

    /**
     * Retrieves all DJ profiles from the database.
     *
     * @return List of all DJ profiles
     */
    @Transactional(readOnly = true)
    public List<DJ> getAllDJProfiles() {

        // Create a list to store DJ profiles
        List<DJ> djs = new ArrayList<>();

        // Retrieve all DJs from MySQL
        djRepository.findAll().forEach(djs::add);

        // Return all DJ profiles
        return djs;
    }

    /**
     * Retrieves a specific DJ profile using its ID.
     *
     * @param djId ID of the DJ
     * @return Optional containing the DJ if found
     */
    @Transactional(readOnly = true)
    public Optional<DJ> getDJProfileById(UUID djId) {

        // Validate DJ ID
        if (djId == null) {
            throw new IllegalArgumentException(
                "DJ ID cannot be null"
            );
        }

        // Find DJ by ID
        return djRepository.findById(djId);
    }
}
