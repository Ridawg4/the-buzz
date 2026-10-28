package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.Contexts.BuzzUserSession;
import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public interface BuzzSessionRepository extends CrudRepository<BuzzUser, UUID> {

    @Modifying
    @Transactional
    @Query(value="INSERT INTO development.sessions(session_id, creation_time, expiry_time, permissions, attributes) VALUES (UUID_TO_BIN(?1), ?2, ?3, ?4, ?5)", nativeQuery = true)
    public void addSession(String id, Timestamp creationTime, Timestamp lastAccessedTime, String permissions, String attributes);

    @Query(value = "SELECT BIN_TO_UUID(session_id) AS session_id, creation_time, expiry_time, permissions, attributes  FROM development.sessions WHERE session_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public BuzzUserSession getSessionById(String id);

    @Modifying
    @Transactional
    @Query(value="UPDATE development.sessions SET session_id = UUID_TO_BIN(?1), creation_time = ?2, expiry_time = ?3, permissions = ?4, attributes = ?5 WHERE session_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public void updateSession(String id, Timestamp creationTime, Timestamp lastAccessedTime, String permissions, String attributes);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM development.sessions WHERE session_id = UUID_TO_BIN(?1)", nativeQuery = true)
    public void deleteSession(String id);
}
