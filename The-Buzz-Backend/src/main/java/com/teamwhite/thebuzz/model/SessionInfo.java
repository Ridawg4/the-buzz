package com.teamwhite.thebuzz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
public class SessionInfo {
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "session_id")
    private UUID sessionId;
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "owner_id")
    private UUID ownerId;
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "needs_refresh")
    private boolean needsRefresh;

    public SessionInfo() {
    }

    /**
     * Creates a new SessionInfo object
     * @param sessionId String the session the information refers to
     * @param ownerId String the owner of this session information
     * @param needsRefresh boolean if the session needs to be refreshed
     */
    public SessionInfo(String sessionId, String ownerId, boolean needsRefresh) {
        this.sessionId = UUID.fromString(sessionId);
        this.ownerId = UUID.fromString(ownerId);
        this.needsRefresh = needsRefresh;
    }

    /**
     * Gets the Session associated to this SessionInfo
     * @return String sessionId
     */
    public String getId() {
        return sessionId.toString();
    }

    /**
     * Sets the sessionId associated to this SessionInfo
     * @param id String The session's ID
     */
    public void setId(String id) {
        this.sessionId = UUID.fromString(id);
    }

    /**
     * Gets the ownerId associated to this SessionInfo
     * @return String The owner's ID for this SessionInfo
     */
    public String getOwnerId() {
        return ownerId.toString();
    }

    /**
     * Sets the ownerId associated to this SessionInfo
     * @param ownerId String owner's ID
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = UUID.fromString(ownerId);
    }

    /**
     * Gets the needsRefresh value associated to this SessionInfo
     * @return boolean if the session needs refreshing
     */
    public boolean isNeedsRefresh() {
        return needsRefresh;
    }

    /**
     * Sets the needsRefresh value associated to this SessionInfo
     * @param needsRefresh boolean does the session need to be refreshed?
     */
    public void setNeedsRefresh(boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }
}
