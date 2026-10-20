package com.teamwhite.thebuzz.Contexts;

import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.teamwhite.thebuzz.Exceptions.InvalidPermissionsObjectException;
import com.teamwhite.thebuzz.model.BuzzUser;
import com.teamwhite.thebuzz.model.SessionPermissions;
import jakarta.json.*;
import jakarta.persistence.Column;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.datatype.jsonp.JSONPModule;

import java.io.File;
import java.io.StringReader;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import static com.teamwhite.thebuzz.Resources.ResourcePaths.DEFAULT_USER_PERMISSIONS_LOCATION;

public class BuzzUserSession implements Session {
    @Id
    @Column(name = "session_id")
    private String id;
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @Column(name = "creation_time")
    private Timestamp creationTime;
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @Column(name = "expiry_time")
    private Timestamp lastAccessedTime;
    @JdbcTypeCode(SqlTypes.DURATION)
    private Duration maxUnaccessedInterval;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissions")
    private SessionPermissions permissions;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes")
    private JsonObject attributes;

    private final BasicPolymorphicTypeValidator.Builder builder = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(Timestamp.class)
            .allowIfSubType(SessionPermissions.class)
            .allowIfSubType(BuzzUser.class);
    private final JsonMapper SECURITY_MAPPER =
            JsonMapper.builder()
                    .addModules(
                            SecurityJacksonModules.getModules(
                                    BuzzUserSession.class.getClassLoader(), builder
                            )
                    )
                    .addModule(new JSONPModule())
                    .build();

    public BuzzUserSession(SessionPermissions permissions) {
        id = generateId();
        if(permissions == null) {
            this.permissions = new SessionPermissions(new File(DEFAULT_USER_PERMISSIONS_LOCATION));
        }
        try {
            this.permissions = permissions;
        } catch (InvalidPermissionsObjectException invalidPermissions) {
            Logger.getGlobal().warning("invalid permissions, resetting");
            this.permissions = new SessionPermissions(new File(DEFAULT_USER_PERMISSIONS_LOCATION));
        }
        creationTime = Timestamp.from(Instant.now());
        lastAccessedTime = Timestamp.from(Instant.now());
        this.attributes = JsonObject.EMPTY_JSON_OBJECT;
    }

    public BuzzUserSession(String id, LocalDateTime creationTime, LocalDateTime lastAccessedTime, String permissions, String attributes) {
        this.id = id;
        this.permissions = new SessionPermissions(permissions);
        this.creationTime = Timestamp.valueOf(creationTime);
        this.lastAccessedTime = Timestamp.valueOf(lastAccessedTime);
        this.attributes = Json.createReader(new StringReader(attributes)).readObject();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String changeSessionId() {
        id = generateId();
        return id;
    }

    @Override
    public @Nullable <T> T getAttribute(String attributeName) {
        if(attributes.containsKey(attributeName)) {
                if(attributeName.equals(HttpSessionSecurityContextRepository
                        .SPRING_SECURITY_CONTEXT_KEY)) {
                    SecurityContext context = null;
                    try {
                        context = SECURITY_MAPPER.readValue(attributes.getString(attributeName), SecurityContextImpl.class);
                    } catch (Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                    return (T) context;
                }
                if(attributeName.equals("SPRING_SECURITY_SAVED_REQUEST")) {
                    DefaultSavedRequest savedRequest = null;
                    try {
                        savedRequest = new JsonMapper().readValue(attributes.getString(attributeName), DefaultSavedRequest.class);
                    } catch (Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                    return (T) savedRequest;
                }
                else {
                    return null;
                }
        }
        return null;
    }

    public JsonObject getAllAttributs() {
        return attributes;
    }

    @Override
    public <T> T getRequiredAttribute(String name) {
        return (T) attributes.get(name);
    }

    @Override
    public <T> T getAttributeOrDefault(String name, T defaultValue) {
        if(attributes.containsKey(name)) {
            return (T) attributes.get(name);
        }
        return defaultValue;
    }

    @Override
    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        JsonObjectBuilder builder = Json.createObjectBuilder(attributes)
                .add(attributeName, SECURITY_MAPPER.writeValueAsString(attributeValue));
//                .add(attributeName + "_CLASS", className.substring(6, className.length() - 1) + ".class");
        attributes = builder.build();
    }

    @Override
    public void removeAttribute(String attributeName) {
        JsonObjectBuilder builder = Json.createObjectBuilder(attributes)
                .remove(attributeName);
        attributes = builder.build();
    }

    @Override
    public Instant getCreationTime() {
        return creationTime.toInstant();
    }

    public Timestamp getCreationTimeAsTimestamp() {
        return creationTime;
    }

    @Override
    public void setLastAccessedTime(Instant lastAccessedTime) {
        this.lastAccessedTime = Timestamp.from(Instant.now());;
    }

    @Override
    public Instant getLastAccessedTime() {
        return lastAccessedTime.toInstant();
    }

    public Timestamp getLastAccessedTimeAsTimestamp() {
        return lastAccessedTime;
    }

    @Override
    public void setMaxInactiveInterval(Duration interval) {
        maxUnaccessedInterval = interval;
    }

    @Override
    public Duration getMaxInactiveInterval() {
        return maxUnaccessedInterval;
    }

    @Override
    public boolean isExpired() {
        Duration durationBetweenLastAccessAndNow = Duration.between(lastAccessedTime.toInstant(), Instant.now());
        if(maxUnaccessedInterval.compareTo(durationBetweenLastAccessAndNow) > 0) {
            return true;
        }
        return false;
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    public SessionPermissions getPermissions() {
        return permissions;
    }
}
