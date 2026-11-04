package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.ShowLikeRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShowLikeService {

    private final ShowLikeRepository showLikeRepository;

    public ShowLikeService(ShowLikeRepository showLikeRepository) {
        this.showLikeRepository = showLikeRepository;
    }

    public void likeShow(String userId, String showId) {
        UUID.fromString(userId);
        UUID.fromString(showId);

        int rowsInserted = showLikeRepository.likeShow(userId, showId);

        if (rowsInserted != 1) {
            throw new IllegalStateException("Show was not liked");
        }
    }

    public boolean unlikeShow(String userId, String showId) {
        UUID.fromString(userId);
        UUID.fromString(showId);

        return showLikeRepository.unlikeShow(userId, showId) > 0;
    }
}
