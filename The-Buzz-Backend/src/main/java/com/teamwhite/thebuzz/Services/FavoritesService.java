
package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.UserRepository;
import com.teamwhite.thebuzz.model.BuzzUser;

import jakarta.json.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoritesService {

    private final UserRepository userRepository;
    private final PermissionService permissionService;

    // Connect the service to our existing repository
    public FavoritesService(UserRepository userRepository, PermissionService permissionService) {
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    // Find the user's current favorites
    private BuzzUser getUser(String userId) {

        List<Object[]> records =
                userRepository.getUserRecordByUserId(userId);

        if (records.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Object[] row = records.get(0);

        String permissionString = permissionService.getPermissionById((int) row[12]).getSessionPermissionsAsJsonObject().toString();


        return new BuzzUser(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                Timestamp.valueOf((LocalDateTime) row[4]),
                (String) row[5],
                (String) row[6],
                (String) row[7],
                (String) row[8],
                row[9] == null ? "{}" : row[9].toString(),
                row[10] == null ? "{}" : row[10].toString(),
                row[11] == null ? "{}" : row[11].toString(),
                permissionString
        );
    }

    // ADD FAVORITE
    public boolean addFavorite(String userId, String showId) {

        BuzzUser user = getUser(userId);

        JsonObject favorites = user.getFavoritesAsJson();

        JsonArray shows = favorites.getJsonArray("shows");

        if (shows == null) {
            shows = Json.createArrayBuilder().build();
        }

        // Prevent duplicate favorites
        for (JsonValue show : shows) {
            if (show.toString().equals(
                    Json.createValue(showId).toString())) {
                return false;
            }
        }

        // Copy existing favorites and add the new show
        JsonArrayBuilder arrayBuilder =
                Json.createArrayBuilder(shows);

        arrayBuilder.add(showId);

        JsonObject updatedFavorites =
                Json.createObjectBuilder(favorites)
                        .add("shows", arrayBuilder)
                        .build();

        // Save to the database
        userRepository.updateUserFavoritesListByUserId(
                userId, updatedFavorites.toString());

        return true;
    }

    // REMOVE FAVORITE
    public boolean removeFavorite(String userId, String showId) {

        BuzzUser user = getUser(userId);

        JsonObject favorites = user.getFavoritesAsJson();

        JsonArray shows = favorites.getJsonArray("shows");

        if (shows == null) {
            return false;
        }

        JsonArrayBuilder arrayBuilder =
                Json.createArrayBuilder();

        boolean removed = false;

        for (JsonValue show : shows) {

            if (show.toString().equals(
                    Json.createValue(showId).toString())) {
                removed = true;
            } else {
                arrayBuilder.add(show);
            }
        }

        if (!removed) {
            return false;
        }

        JsonObject updatedFavorites =
                Json.createObjectBuilder(favorites)
                        .add("shows", arrayBuilder)
                        .build();

        // Save the updated favorites
        userRepository.updateUserFavoritesListByUserId(
                userId, updatedFavorites.toString());

        return true;
    }
}
