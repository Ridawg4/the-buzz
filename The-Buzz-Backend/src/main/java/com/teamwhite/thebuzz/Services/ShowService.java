

package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.ShowRepository;
import com.teamwhite.thebuzz.model.Show;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for creating, modifying, and deleting radio shows.
 */
@Service
public class ShowService {

    private final ShowRepository showRepository;

    // Constructor connects the service to the repository
    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    /**
     * Creates a new radio show listing.
     *
     * @param show Show information
     * @return true if the show was created
     */
    public boolean createShow(Show show) {

        // Validate show information
        validateShow(show);

        // Generate a unique ID for the new show
        show.setShowId(UUID.randomUUID());

        // Save the show to the database
        showRepository.save(show);

        return true;
    }

    /**
     * Modifies an existing radio show listing.
     *
     * @param showId ID of the show to update
     * @param updatedShow New show information
     * @return true if the show was updated
     */
    @Transactional
    public boolean updateShow(UUID showId, Show updatedShow) {

        // Check that the show ID is provided
        if (showId == null) {
            throw new IllegalArgumentException(
                "Show ID cannot be null"
            );
        }

        // Validate the new show information
        validateShow(updatedShow);

        // Find the existing show
        Show existingShow = showRepository.findById(showId)
                .orElse(null);

        if (existingShow == null) {
            return false;
        }

        // Update show information
        existingShow.setTitle(updatedShow.getTitle());
        existingShow.setDescription(updatedShow.getDescription());
        existingShow.setDjId(updatedShow.getDjId());
        existingShow.setStartTime(updatedShow.getStartTime());
        existingShow.setEndTime(updatedShow.getEndTime());
        existingShow.setShowDate(updatedShow.getShowDate());

        // Save updated information
        showRepository.save(existingShow);

        return true;
    }

    /**
     * Deletes an existing radio show listing.
     *
     * @param showId ID of the show to delete
     * @return true if the show was deleted
     */
    @Transactional
    public boolean deleteShow(UUID showId) {

        // Check that the show ID is provided
        if (showId == null) {
            throw new IllegalArgumentException(
                "Show ID cannot be null"
            );
        }

        // Check whether the show exists
        if (!showRepository.existsById(showId)) {
            return false;
        }

        // Delete the show
        showRepository.deleteById(showId);

        return true;
    }

    /**
     * Validates show information before creating or updating.
     *
     * @param show Show to validate
     */
    private void validateShow(Show show) {

        // Make sure show information is provided
        if (show == null) {
            throw new IllegalArgumentException(
                "Show information cannot be null"
            );
        }

        // Validate title
        if (show.getTitle() == null ||
                show.getTitle().isBlank() ||
                show.getTitle().length() > 255) {

            throw new IllegalArgumentException(
                "Show title must be between 1 and 255 characters"
            );
        }

        // Validate description
        if (show.getDescription() != null &&
                show.getDescription().length() > 1000) {

            throw new IllegalArgumentException(
                "Description cannot exceed 1000 characters"
            );
        }

        // Validate DJ ID
        if (show.getDjId() == null) {
            throw new IllegalArgumentException(
                "DJ ID cannot be null"
            );
        }

        // Validate show date
        if (show.getShowDate() == null) {
            throw new IllegalArgumentException(
                "Show date cannot be null"
            );
        }

        // Validate start and end times
        if (show.getStartTime() == null ||
                show.getEndTime() == null) {

            throw new IllegalArgumentException(
                "Start and end times are required"
            );
        }

        // Make sure end time is after start time
        if (!show.getEndTime().after(show.getStartTime())) {
            throw new IllegalArgumentException(
                "End time must be after start time"
            );
        }
    }
}
