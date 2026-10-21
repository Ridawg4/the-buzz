package com.teamwhite.thebuzz.Contexts;

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

/**
 * Handles the UserSession object and session handling functions
 */
public class BuzzUserSession implements Session {
    @Id
    @Column(name = "session_id")
    private UUID id;
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @Column(name = "creation_time")
    private Timestamp creationTime;
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @Column(name = "expiry_time")
    // Variable name is different enough from the column name that mapping to the column is required
    private Timestamp lastAccessedTime;
    @JdbcTypeCode(SqlTypes.DURATION)
    private Duration maxUnaccessedInterval;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissions")
    private SessionPermissions permissions;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes")
    private JsonObject attributes;

    /**
     * Required to map BuzzUser into JSON that can be recreated into an SecurityContextHandler object
     * Without this, the JsonMapper refuses for security purposes, since the files are outside
     * the Spring Security class and therefore aren't trusted by default
     */
    private final BasicPolymorphicTypeValidator.Builder POLY_TYPE_VALIDATOR_BUILDER =
            BasicPolymorphicTypeValidator.builder()
                // Allows the Timestamp class to be mapped
                .allowIfSubType(Timestamp.class)
                // Allows the SessionPermissions class to be mapped
                .allowIfSubType(SessionPermissions.class)
                // Allows the BuzzUser class to be mapped
                .allowIfSubType(BuzzUser.class);

    private final JsonMapper SECURITY_MAPPER =
            JsonMapper.builder()
                    .addModules(
                            // Adds the POLY_TYPE_VALIDATOR_BUILDER and
                            // the current session class to the builder alongside Spring Security
                            SecurityJacksonModules.getModules(
                                    BuzzUserSession.class.getClassLoader(), POLY_TYPE_VALIDATOR_BUILDER
                            )
                    )
                    .addModule(new JSONPModule()) // Adds JsonObject handling to JsonMapper
                    .build();
    /**
     * Creates a new in memory session with everything being generated
     * This gives the session holder only default user permissions
     * @TODO IMPLEMENT CHANGING DEFAULT PERMISSIONS
     */
    public BuzzUserSession() {
        id = generateId();
        this.permissions = new SessionPermissions();
        creationTime = Timestamp.from(Instant.now());
        lastAccessedTime = Timestamp.from(Instant.now());
        this.attributes = JsonObject.EMPTY_JSON_OBJECT;
    }

    /**
     * Creates a new in memory session with provided permissions, everything else being generated
     * @param permissions SessionPermissions the permissions the session holder has
     */
    public BuzzUserSession(SessionPermissions permissions) {
        id = generateId();
        if(permissions == null) {
            throw new RuntimeException("Permissions not provided");
        }
        try {
            this.permissions = permissions;
        } catch (InvalidPermissionsObjectException invalidPermissions) {
            Logger.getGlobal().warning("invalid permissions provided and was unable to be assigned");
        }
        creationTime = Timestamp.from(Instant.now());
        lastAccessedTime = Timestamp.from(Instant.now());
        // Attributes are used for redirecting and session ownership at runtime
        this.attributes = JsonObject.EMPTY_JSON_OBJECT;
    }

    /**
     * Creates a user session in memory that has every field provided
     * @param id String UUID in string representation
     * @param creationTime LocalDateTime time the session was created
     * @param lastAccessedTime LocalDateTime time the session was last retrieved
     * @param permissions String SessionPermission in an unformatted JSON String
     * @param attributes String attributes in an unformatted JSON String
     */
    public BuzzUserSession(String id, LocalDateTime creationTime, LocalDateTime lastAccessedTime, String permissions, String attributes) {
        this.id = UUID.fromString(id);
        this.permissions = new SessionPermissions(permissions);
        // Database stores LocalDateTime, we use Timestamp, so we need to convert them
        this.creationTime = Timestamp.valueOf(creationTime);
        this.lastAccessedTime = Timestamp.valueOf(lastAccessedTime);
        this.attributes = Json.createReader(new StringReader(attributes)).readObject();
    }

    /**
     * Gets the session ID
     * @return String the session ID in String representation
     */
    @Override
    public String getId() {
        return id.toString();
    }

    /**
     * Generates a new session ID
     * @return String the session ID in String representation
     */
    @Override
    public String changeSessionId() {
        id = generateId();
        return id.toString();
    }

    /**
     * Gets a specific attribute as stored in the session attributes
     * @param attributeName the name of the attribute to get
     * @return T runtime class generated from the session attribute
     * @param <T> Generic runtime class to return
     */
    @Override
    public @Nullable <T> T getAttribute(String attributeName) {
        if(attributes.containsKey(attributeName)) {
                // Checks if the attribute is a SecurityContextImpl class
                if(attributeName.equals(HttpSessionSecurityContextRepository
                        .SPRING_SECURITY_CONTEXT_KEY)) {
                    SecurityContext context = null;
                    try {
                        // Requires the mapper, otherwise the class cannot be recreated
                        context = SECURITY_MAPPER.readValue(attributes.getString(attributeName), SecurityContextImpl.class);
                    } catch (Exception e) {
                        // In case the SECURITY_MAPPER fails, it catches and logs the result
                        Logger.getGlobal().info(e.getMessage());
                    }
                    // Returns the object converted to the class requested
                    return (T) context;
                }
                // Checks if the key is for Saved Requests, or redirects
                // Used to return the user to the previous page when needing to authenticate
                if(attributeName.equals("SPRING_SECURITY_SAVED_REQUEST")) {
                    DefaultSavedRequest savedRequest = null;
                    try {
                        // Requires the mapper, otherwise the class cannot be recreated
                        savedRequest = new JsonMapper().readValue(attributes.getString(attributeName), DefaultSavedRequest.class);
                    } catch (Exception e) {
                        // In case the SECURITY_MAPPER fails, it catches and logs the result
                        Logger.getGlobal().info(e.getMessage());
                    }
                    // Returns the object converted to the class requested
                    return (T) savedRequest;
                }
                // For now, those two conversions are the only ones implemented
                // Other attributes are not mapped
                else {
                    return null;
                }
        }
        // The same result happens if the attribute does not exist
        return null;
    }

    /**
     * Gets all attributes
     * @return JsonObject the attributes
     */
    public JsonObject getAllAttributs() {
        return attributes;
    }

    /**
     * Gets a required attribute ??
     * Unsure of where this is called, needs further investigation
     * @param name the attribute name
     * @return T class requested found in the attributes
     * @param <T> Generic class of the type requested
     */
    @Override
    public <T> T getRequiredAttribute(String name) {
        return (T) attributes.get(name);
    }

    /**
     * Returns either a default value, or the attribute if it exists in the session
     * Unsure of where this is called, needs further investigation
     *
     * @param name the attribute name
     * @param defaultValue a default value to return instead
     * @return T runtime type requested
     * @param <T> Generic type requested
     */
    @Override
    public <T> T getAttributeOrDefault(String name, T defaultValue) {
        if(attributes.containsKey(name)) {
            return (T) attributes.get(name);
        }
        return defaultValue;
    }

    /**
     * Gets all available attribute names that exist within the session
     * @return Set<String> all attribute names
     */
    @Override
    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    /**
     * Adds an attribute into the session
     * @param attributeName the attribute name to set
     * @param attributeValue the value of the attribute to set. If null, the attribute
     * will be removed.
     */
    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        JsonObjectBuilder builder = Json.createObjectBuilder(attributes);
        // Checks if value is null, which indicates to remove the attribute
        // No clue who in Spring made that decision, but we must follow it
        if(attributeValue == null) {
            // If the value already exists in our set, the builder has it as well
            if(attributes.containsKey(attributeName)) {
                builder.remove(attributeName);
            }
            // if the value is not null, add it to the builder
        } else {
            builder.add(attributeName, SECURITY_MAPPER.writeValueAsString(attributeValue));
        }
        // Set the JsonObject of attributes to the newly built JsonObject
        attributes = builder.build();
    }

    /**
     * Removes an attribute from the session
     * @param attributeName the name of the attribute to remove
     */
    @Override
    public void removeAttribute(String attributeName) {
        JsonObjectBuilder builder = Json.createObjectBuilder(attributes);
        // If the key doesn't exist, and you attempt to remove it, a
        // NullPointerException will be thrown
        if(attributes.containsKey(attributeName)) {
            builder.remove(attributeName);
        }
        attributes = builder.build();
    }

    /**
     * Gets the Instant that the session was created
     * @return Instant when the session was created
     */
    @Override
    public Instant getCreationTime() {
        return creationTime.toInstant();
    }

    /**
     * Gets the time as a Timestamo
     * @return Timestamp when the session was created
     */
    public Timestamp getCreationTimeAsTimestamp() {
        return creationTime;
    }

    /**
     * Set the last accessed time of the session to the provided time
     * @param lastAccessedTime the last accessed time
     */
    @Override
    public void setLastAccessedTime(Instant lastAccessedTime) {
        this.lastAccessedTime = Timestamp.from(Instant.now());;
    }

    /**
     * Gets the last time the session was accessed
     * @return Instant the time the session was last accessed
     */
    @Override
    public Instant getLastAccessedTime() {
        return lastAccessedTime.toInstant();
    }

    /**
     * Get the last accessed time as a Timestamp object
     * @return Timestamp the time the session was last accessed
     */
    public Timestamp getLastAccessedTimeAsTimestamp() {
        return lastAccessedTime;
    }

    /**
     * Sets the maximum time the session should live after last access before expiring it
     * @param interval the amount of time that the {@link Session} should be kept alive
     * between client requests.
     */
    @Override
    public void setMaxInactiveInterval(Duration interval) {
        maxUnaccessedInterval = interval;
    }

    /**
     * Gets the maximum interval time
     * @return Duration time duration for the maximum interval time
     */
    @Override
    public Duration getMaxInactiveInterval() {
        return maxUnaccessedInterval;
    }

    /**
     * Determines if the session is expired by comparing the last accessed time and seeing
     * if that plus the current time is longer than the maxUnaccessedInterval set
     * @return boolean if the session is expired
     */
    @Override
    public boolean isExpired() {
        // So Timestamp is not able to convert to Duration, but Instant can, which is why its converted
        Duration durationBetweenLastAccessAndNow = Duration.between(lastAccessedTime.toInstant(), Instant.now());
        // We also cannot compare Instant with Duration, so that's why we create another Duration
        if(maxUnaccessedInterval.compareTo(durationBetweenLastAccessAndNow) > 0) {
            return true;
        }
        return false;
    }

    /**
     * Generates a UUID
     * @return UUID new UUID
     */
    private UUID generateId() {
        return UUID.randomUUID();
    }

    /**
     * Gets the SessionPermissions for this session
     * @return SessionPermissions all permissions granted to this session
     */
    public SessionPermissions getPermissions() {
        return permissions;
    }
}
