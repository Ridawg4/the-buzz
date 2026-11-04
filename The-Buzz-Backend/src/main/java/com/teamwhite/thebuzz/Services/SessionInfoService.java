package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.SessionInfoRepository;
import com.teamwhite.thebuzz.model.SessionInfo;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SessionInfoService {
    private final SessionInfoRepository repository;

    public SessionInfoService(SessionInfoRepository repository) {
        this.repository = repository;
    }

    /**
     * Updates a specific session's NeedsRefresh value
     * Used for determining if an authentication object needs to be refreshed when loading a session
     * @param sessionId String session ID to be updated
     * @param needRefresh boolean new value
     */
    public void updateNeedsRefreshBySessionId(String sessionId, boolean needRefresh) {
        repository.updateNeedsRefreshBySessionId(sessionId, needRefresh);
    }

    /**
     * Updates a specific session's needsToDeleteAuth value
     * Used for determining if an authentication object needs to be removed
     * @param sessionId String session ID to be updated
     * @param needsToDeleteAuth boolean new value
     */
    public void updateDeleteAuthToken(String sessionId, boolean needsToDeleteAuth) {
        repository.updateNeedsToDeleteAuthBySessionId(sessionId, needsToDeleteAuth);
    }

    /**
     * Gets all sessions associated with a specific user
     * @param ownerId String userID of the sessions we want
     * @return SessionInfo[] All sessionInfo's associated with the ownerId
     */
    public SessionInfo[] getAllSessionsInfoByOwnerId(String ownerId) {
        List<Object[]> returnedValues = repository.getAllSessionsInfoByOwnerId(ownerId);

        // If no values were returned, no users exist with that email
        if(returnedValues == null) { //@TODO CHANGE EXCEPTION
            throw new RuntimeException("results are null when expected results at getUserRecordByEmail()");
        }
        if(returnedValues.isEmpty()) { //@TODO CHANGE EXCEPTION
            return null;
        }

        SessionInfo[] sessionInfos = new SessionInfo[returnedValues.size()];
        int index = 0;

        // I didn't want to rewrite as a real for loop, so added index on top out of spite
        for(Object[] objFromQuery : returnedValues) {
            SessionInfo info = new SessionInfo((String) objFromQuery[0], (String) objFromQuery[1],
                    (boolean) objFromQuery[2], (boolean) objFromQuery[3]);
            sessionInfos[index] = info;
            index++;
        }

        return sessionInfos;
    }

    /**
     * Gets a specific SessionInfo by the sessionId
     * @param sessionId String sessionId used to find a specific session
     * @return SessionInfo
     */
    public SessionInfo getSessionInfoBySessionId(String sessionId) {
        List<Object[]> returnedValues = repository.getSessionInfoBySessionId(sessionId);

        // If no values were returned, no users exist with that email
        if(returnedValues == null) { //@TODO CHANGE EXCEPTION
            throw new RuntimeException("results are null when expected results at getUserRecordByEmail()");
        }
        // If more than one record was returned, we can't know which user they are referring to
        // This also indicates a database failure, as schema denotes unique emails
        if(returnedValues.size() > 1) { //@TODO CHANGE EXCEPTION
            throw new RuntimeException("results are more than one");
        }
        if(returnedValues.isEmpty()) { //@TODO CHANGE EXCEPTION
            return null;
        }

        // Magic number, but List<T> does not have getFirst() method, this recreates that result
        Object[] objFromQuery = returnedValues.get(0);
        /**
         * This looks disgusting, but is the most efficient way to map them explicitly, as the
         * deserializer cannot map Timestamp, JsonObject, or UUID values automatically
         */

        SessionInfo info = new SessionInfo( (String) objFromQuery[0], (String) objFromQuery[1],
                (boolean) objFromQuery[2], (boolean) objFromQuery[3]);
        return info;
    }

    /**
     * Adds a SessionInfo entry to the database
     * @param sessionId The ID of the session we are adding
     * @param ownerId The ID of the owner for that session
     * @param needsRefresh If the session needs to be refreshed
     */
    public void addSessionInfo(String sessionId, String ownerId, boolean needsRefresh, boolean needsToDeleteAuth) {
        try {
            // Error is here
            /**
             * HHH000247: ErrorCode: 1062, SQLState: 23000
             * 2026-11-03T17:41:51.903-05:00  WARN 62810 ---
             * [The-Buzz-Backend] [nio-8080-exec-1] org.hibernate.orm.jdbc.error
             * : Duplicate entry '\x08q\xA1;Mv@!\xBEfq\x18\xCB\x0A\x90\xE8' for key 'sessions.PRIMARY'
             */
            repository.addSessionInfo(sessionId, ownerId, needsRefresh, needsToDeleteAuth);
        } catch (Exception e) {
            Logger.getGlobal().info("Error adding session info: " + e.getMessage());
        }
    }

    /**
     * Deletes a SessionInfo entry with a given sessionId
     * @param sessionId String sessionId
     */
    public void deleteSessionInfo(String sessionId) {
        repository.deleteSessionInfoBySessionId(sessionId);
    }
}
