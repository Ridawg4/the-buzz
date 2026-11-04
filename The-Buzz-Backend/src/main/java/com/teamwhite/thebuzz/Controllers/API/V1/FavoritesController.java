
p
package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.FavoritesService;
import com.teamwhite.thebuzz.model.BuzzUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    // Adds a show to the logged-in user's favorites
    @PostMapping("/add-favorite")
    public ResponseEntity<Void> addFavorite(
            @RequestParam("show_id") String showId,
            Authentication userAuth) {

        // Check that the user is logged in
        if (userAuth == null
                || !(userAuth.getPrincipal() instanceof BuzzUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check that the show ID is valid
        if (showId == null || showId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Add the show to the user's favorites
        boolean added = favoritesService.addFavorite(
                user.getUserId(),
                showId
        );

        if (!added) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Removes a show from the logged-in user's favorites
    @DeleteMapping("/remove-favorite")
    public ResponseEntity<Void> removeFavorite(
            @RequestParam("show_id") String showId,
            Authentication userAuth) {

        // Check that the user is logged in
        if (userAuth == null
                || !(userAuth.getPrincipal() instanceof BuzzUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check that the show ID is valid
        if (showId == null || showId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Remove the show from the user's favorites
        boolean removed = favoritesService.removeFavorite(
                user.getUserId(),
                showId
        );

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
