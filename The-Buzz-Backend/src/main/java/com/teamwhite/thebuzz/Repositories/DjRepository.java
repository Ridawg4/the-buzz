package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.DJ;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for handling DJ database operations.
 */
@Repository
public interface DjRepository extends CrudRepository<DJ, UUID> {

    /**
     * Updates a DJ's profile information.
     *
     * @param userId ID of the DJ's user account
     * @param djName New DJ name
     * @param genre New music genre
     * @param pfpLocation New profile picture location
     * @return Number of rows updated
     */
    @Modifying
    @Transactional
    @Query(
        value = "UPDATE development.djs " +
                "SET dj_name = ?2, " +
                "genre = ?3, " +
                "dj_pfp_location = ?4 " +
                "WHERE user_id = UUID_TO_BIN(?1)",
        nativeQuery = true
    )
    int updateDJProfile(
        String userId,
        String djName,
        String genre,
        String pfpLocation
    );
}