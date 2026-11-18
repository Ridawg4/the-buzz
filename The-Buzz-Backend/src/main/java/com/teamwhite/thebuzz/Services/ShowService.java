package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.ShowRepository;
import com.teamwhite.thebuzz.model.Show;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Service for creating, modifying, deleting,
 * and viewing radio shows and showtimes.
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
     */
    public boolean createShow(Show show) {

        validateShow(show);

        show.setShowId(UUID.randomUUID());

        // New shows are visible by default
        show.setIsVisible(true);

        showRepository.save(show);

        return true;
    }

    /**
     * Modifies an existing radio show listing.
     */
    @Transactional
    public boolean updateShow(UUID showId, Show updatedShow) {

        if (showId == null) {
            throw new IllegalArgumentException(
                "Show ID cannot be null"
            );
        }

        validateShow(updatedShow);

        Show existingShow = showRepository.findById(showId)
                .orElse(null);

        if (existingShow == null) {
            return false;
        }

        existingShow.setTitle(updatedShow.getTitle());
        existingShow.setDescription(updatedShow.getDescription());
        existingShow.setDjId(updatedShow.getDjId());
        existingShow.setStartTime(updatedShow.getStartTime());
        existingShow.setEndTime(updatedShow.getEndTime());
        existingShow.setShowDate(updatedShow.getShowDate());

        // Preserve the existing visibility status

        showRepository.save(existingShow);

        return true;
    }

    /**
     * Hides a show from the public schedule.
     */
    @Transactional
    public boolean hideShow(UUID showId) {

        if (showId == null) {
            throw new IllegalArgumentException(
                "Show ID cannot be null"
            );
        }

        Show existingShow = showRepository.findById(showId)
                .orElse(null);

        if (existingShow == null) {
            return false;
        }

        existingShow.setIsVisible(false);

        showRepository.save(existingShow);

        return true;
    }

    /**
     * Makes a hidden show visible again.
     */
    @Transactional
    public boolean showShow(UUID showId) {

        if (showId == null) {
            throw new IllegalArgumentException(
                "Show ID cannot be null"
            );
        }

        Show existingShow = showRepository.findById(showId)
                .orElse(null);

        if (existingShow == null) {
            return false;
        }

        existingShow.setIsVisible(true);

        showRepository.save(existingShow);

        return true;
    }

    /**
     * Deletes an existing radio show listing.
     */
    @Transactional
    public boolean deleteShow(UUID showId) {

        if (showId == null) {
            throw new IllegalArgumentException(
                "Show ID cannot be null"
            );
        }

        if (!showRepository.existsById(showId)) {
            return false;
        }

        showRepository.deleteById(showId);

        return true;
    }

    /**
     * Retrieves all radio shows from the database.
     */
    @Transactional(readOnly = true)
    public List<Show> getAllShows() {

        List<Show> shows = new ArrayList<>();

        showRepository.findAll().forEach(shows::add);

        return shows;
    }

    /**
     * Retrieves visible shows ordered by date and start time.
     */
    @Transactional(readOnly = true)
    public List<Show> getShowSchedule() {

        List<Show> shows = getAllShows();

        // Only display visible shows
        shows.removeIf(show ->
            !Boolean.TRUE.equals(show.getIsVisible())
        );

        // Sort by show date, then start time
        shows.sort(
            Comparator.comparing(
                Show::getShowDate,
                Comparator.nullsLast(
                    Comparator.naturalOrder()
                )
            ).thenComparing(
                Show::getStartTime,
                Comparator.nullsLast(
                    Comparator.naturalOrder()
                )
            )
        );

        return shows;
    }

    /**
     * Validates show information before creating or updating.
     */
    private void validateShow(Show show) {

        if (show == null) {
            throw new IllegalArgumentException(
                "Show information cannot be null"
            );
        }

        if (show.getTitle() == null ||
                show.getTitle().isBlank() ||
                show.getTitle().length() > 255) {

            throw new IllegalArgumentException(
                "Show title must be between 1 and 255 characters"
            );
        }

        if (show.getDescription() != null &&
                show.getDescription().length() > 1000) {

            throw new IllegalArgumentException(
                "Description cannot exceed 1000 characters"
            );
        }

        if (show.getDjId() == null) {
            throw new IllegalArgumentException(
                "DJ ID cannot be null"
            );
        }

        if (show.getShowDate() == null) {
            throw new IllegalArgumentException(
                "Show date cannot be null"
            );
        }

        if (show.getStartTime() == null ||
                show.getEndTime() == null) {

            throw new IllegalArgumentException(
                "Start and end times are required"
            );
        }

        if (!show.getEndTime().after(show.getStartTime())) {
            throw new IllegalArgumentException(
                "End time must be after start time"
            );
        }
    }
}
