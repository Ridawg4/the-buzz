package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.SessionInfoRepository;
import com.teamwhite.thebuzz.model.SessionInfo;
import org.springframework.stereotype.Service;
import java.util.List;

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
                    (boolean) objFromQuery[2]);
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
                (boolean) objFromQuery[2]);
        return info;
    }

    /**
     * Adds a SessionInfo entry to the database
     * @param sessionId The ID of the session we are adding
     * @param ownerId The ID of the owner for that session
     * @param needsRefresh If the session needs to be refreshed
     */
    public void addSessionInfo(String sessionId, String ownerId, boolean needsRefresh) {
        repository.addSessionInfo(sessionId, ownerId, needsRefresh);
    }

    /**
     * Deletes a SessionInfo entry with a given sessionId
     * @param sessionId String sessionId
     */
    public void deleteSessionInfo(String sessionId) {
        repository.deleteSessionInfoBySessionId(sessionId);
    }
}
