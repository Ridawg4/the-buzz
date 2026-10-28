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

    public SessionInfo(String sessionId, String ownerId, boolean needsRefresh) {
        this.sessionId = UUID.fromString(sessionId);
        this.ownerId = UUID.fromString(ownerId);
        this.needsRefresh = needsRefresh;
    }

    public String getId() {
        return sessionId.toString();
    }

    public void setId(String id) {
        this.sessionId = UUID.fromString(id);
    }


    public String getOwnerId() {
        return ownerId.toString();
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = UUID.fromString(ownerId);
    }

    public boolean isNeedsRefresh() {
        return needsRefresh;
    }

    public void setNeedsRefresh(boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }
}
