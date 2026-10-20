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

    public BuzzUserSessionRepository(BuzzSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public BuzzUserSession createSession() {
        return new BuzzUserSession(new SessionPermissions(new File(DEFAULT_USER_PERMISSIONS_LOCATION)));
    }

    @Override
    public void save(BuzzUserSession session) {
        if(repository.getSessionById(session.getId()) == null) {
            repository.addSession(session.getId(), session.getCreationTimeAsTimestamp(), session.getLastAccessedTimeAsTimestamp(), session.getPermissions().asJsonObject().toString(), session.getAllAttributs().toString());
        }
    }

    @Override
    public BuzzUserSession findById(String id) {
        if(!id.isEmpty()) {
            return repository.getSessionById(id);
        } else {
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        repository.deleteSession(id);
    }
}
