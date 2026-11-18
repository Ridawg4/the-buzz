package com.teamwhite.thebuzz.model;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class AccountModeration {

    @Id
    private int moderationId;
    private UUID userId;
    private String moderationDesc;
    private Timestamp dateApplied;
    private UUID moderatorAppliedAction;
    private String moderationSeverity;
    private Timestamp dateExpires;

    private boolean isAcknowledged;

    public AccountModeration() {
    }

    public AccountModeration(int moderationId, String userId, String moderationDesc, LocalDateTime dateApplied,
                             String moderatorAppliedAction, String moderationSeverity,
                             LocalDateTime dateExpires, boolean isAcknowledged) {
        this.moderationId = moderationId;
        this.userId = UUID.fromString(userId);
        this.moderationDesc = moderationDesc;
        this.dateApplied = Timestamp.valueOf(dateApplied);
        this.moderatorAppliedAction = UUID.fromString(moderatorAppliedAction);
        this.moderationSeverity = moderationSeverity;
        this.dateExpires = Timestamp.valueOf(dateExpires);
        this.isAcknowledged = isAcknowledged;
    }

    public boolean isAcknowledged() {
        return isAcknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        isAcknowledged = acknowledged;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getModerationDesc() {
        return moderationDesc;
    }

    public void setModerationDesc(String moderationDesc) {
        this.moderationDesc = moderationDesc;
    }

    public Timestamp getDateApplied() {
        return dateApplied;
    }

    public void setDateApplied(Timestamp dateApplied) {
        this.dateApplied = dateApplied;
    }

    public String getModeratorAppliedAction() {
        return moderatorAppliedAction.toString();
    }

    public void setModeratorAppliedAction(String moderatorAppliedAction) {
        this.moderatorAppliedAction = UUID.fromString(moderatorAppliedAction);
    }

    public String getModerationSeverity() {
        return moderationSeverity;
    }

    public void setModerationSeverity(String moderationSeverity) {
        this.moderationSeverity = moderationSeverity;
    }

    public Timestamp getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(Timestamp dateExpires) {
        this.dateExpires = dateExpires;
    }

    public JsonObject asJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("moderationId", moderationId)
                .add("actedUser", userId.toString())
                .add("moderationDesc", moderationDesc)
                .add("dateApplied", dateApplied.toString())
                .add("moderatorAppliedAction", moderatorAppliedAction.toString())
                .add("moderationSeverity", moderationSeverity)
                .add("dateExpires", dateExpires.toString())
                .add("isAcknowledged", isAcknowledged);

        return builder.build();
    }
}
