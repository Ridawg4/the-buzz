package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.SessionInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionInfoRepository extends CrudRepository<SessionInfo, UUID> {

    @Query(value = "SELECT BIN_TO_UUID(session_id) as session_id, BIN_TO_UUID(owner_id) as owner_id, needs_refresh, needs_to_delete_auth FROM development.session_info WHERE session_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public List<Object[]> getSessionInfoBySessionId(String sessionId);

    @Query(value = "SELECT BIN_TO_UUID(session_id) as session_id, BIN_TO_UUID(owner_id) as owner_id, needs_refresh, needs_to_delete_auth FROM development.session_info WHERE owner_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public List<Object[]> getAllSessionsInfoByOwnerId(String ownerId);

    @Modifying
    @Transactional
    @Query(value="INSERT INTO development.session_info(session_id, owner_id, needs_refresh, needs_to_delete_auth) VALUES (UUID_TO_BIN(?1), UUID_TO_BIN(?2), ?3, ?4)", nativeQuery = true)
    public void addSessionInfo(String sessionId, String userId, boolean needsRefresh, boolean needsToDeleteAuth);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.session_info SET needs_refresh = ?2 WHERE session_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public void updateNeedsRefreshBySessionId(String sessionId, boolean needsRefresh);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.session_info SET needs_to_delete_auth = ?2 WHERE session_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public void updateNeedsToDeleteAuthBySessionId(String sessionId, boolean needsToDeleteAuth);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.session_info SET session_id = UUID_TO_BIN(?2) WHERE session_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public void updateSessionIdBySessionId(String sessionId, String newSessionId);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM development.session_info WHERE session_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public void deleteSessionInfoBySessionId(String sessionId);
}
