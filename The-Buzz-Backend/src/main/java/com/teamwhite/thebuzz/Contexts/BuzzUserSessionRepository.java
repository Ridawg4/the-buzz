package com.teamwhite.thebuzz.Contexts;

import com.teamwhite.thebuzz.Repositories.BuzzSessionRepository;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.Permissions;
import com.teamwhite.thebuzz.model.SessionPermissions;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.logging.Logger;

import static com.teamwhite.thebuzz.Resources.ResourcePaths.DEFAULT_USER_PERMISSIONS_LOCATION;

@Repository
public class BuzzUserSessionRepository implements SessionRepository<BuzzUserSession> {
    private BuzzSessionRepository repository;

    /**
     * Autowired constructor
     * @param repository BuzzSessionRepository reference
     */
    public BuzzUserSessionRepository(BuzzSessionRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new BuzzUserSession without specified SessionPermissions
     * @hidden I wish it would allow me to include SessionPermissions at runtime,
     * but since it's an implemented method, I cannot do that and runtime won't recognize it if I did
     * @return BuzzUserSession created session
     */
    @Override
    public BuzzUserSession createSession() {
        return new BuzzUserSession();
    }

    /**
     * Saves the session into persistent storage or updates persistent storage
     * if the session already exists
     * @param session the {@link org.springframework.session.Session} to save
     */
    @Override
    public void save(BuzzUserSession session) {
        if(repository.getSessionById(session.getId()) == null) {
            repository.addSession(session.getId(), session.getCreationTimeAsTimestamp(), session.getLastAccessedTimeAsTimestamp(), session.getPermissions().asJsonObject().toString(), session.getAllAttributs().toString());
        } else {
            repository.updateSession(session.getId(), session.getCreationTimeAsTimestamp(), session.getLastAccessedTimeAsTimestamp(), session.getPermissions().asJsonObject().toString(), session.getAllAttributs().toString());

        }
    }

    /**
     * Gets the {@link BuzzUserSession} with the associated ID,
     * or null if the value does not exist
     * @param id the {@link org.springframework.session.Session#getId()} to lookup
     * @return BuzzUserSession the session with the ID provided
     */
    @Override
    public BuzzUserSession findById(String id) {
        if(!id.isEmpty()) {
            return repository.getSessionById(id);
        } else {
            // @TODO Throw an error if id is null, not return empty
            return null;
        }
    }

    /**
     * Deletes the session with the given ID from persistent storage
     * @param id the {@link org.springframework.session.Session#getId()} to delete
     */
    @Override
    public void deleteById(String id) {
        repository.deleteSession(id);
    }
}
