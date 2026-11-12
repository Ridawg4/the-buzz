package com.teamwhite.thebuzz.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teamwhite.thebuzz.Contexts.PermissionRetrievalService;
import com.teamwhite.thebuzz.Exceptions.InvalidPermissionsObjectException;
import com.teamwhite.thebuzz.Resources.PermissionValues;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import static com.teamwhite.thebuzz.Resources.ResourcePaths.DEFAULT_USER_PERMISSIONS_LOCATION;

/**
 * SessionPermissions contains all the permissions needed at session runtime for users
 */
public class SessionPermissions implements GrantedAuthority {
    protected int permissionId;
    protected HashMap<String, String> permissions = new HashMap<String, String>();

    /**
     * Creates a SessionPermissions object with the formatted Json String of permissions
     * @param jsonStringToConvert String permissions to use
     */

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public SessionPermissions( String jsonStringToConvert) {
        JsonObject obj = Json.createReader(new StringReader(jsonStringToConvert.trim())).readObject();
        parseJsonAsPermissionsMap(obj);
    }

//    // @TODO NEEDS TO RETURN USERS PERMISSION OBJECT
    public SessionPermissions() {
//        parseJsonAsPermissionsMap(PermissionRetrievalService.getPermissionById(PermissionRetrievalService.getDefaultPermissionGroupIndex()).getSessionPermissionsAsJsonObject());
    }

    /**
     * Creates a SessionPermissions object with the provided permissions
     * @param permissions JsonObject permissions
     */
    public SessionPermissions(JsonObject permissions) {
        parseJsonAsPermissionsMap(permissions);
    }

    /**
     * Converts the JsonObject representation of the SessionPermissions to its class representation
     * @param json JsonObject of the SessionPermissions values
     */
    private void parseJsonAsPermissionsMap(JsonObject json) {
        // If the value "permission_id" doesn't exist, the JsonObject is invalid
        if(!json.containsKey("permission_id")) {
            throw new InvalidPermissionsObjectException("Json mapping does not contain \"permission_id\"" +
                    " field");
        }
        // If the value "permission_data" doesn't exist, the JsonObject is invalid
        if(!json.containsKey("permission_data")) {
            throw new InvalidPermissionsObjectException("Json mapping does not contain \"permission_data\"" +
                    " field");
        }
        // Creates a new JsonObject representing the actual permission data
        JsonObject permissionData = null;
        try {
            // This has thrown errors before, but I do not believe it happens anymore
            // Either way, this stays just cause
            permissionData = json.getJsonObject("permission_data");
            permissionId = json.getInt("permission_id");
        } catch (ClassCastException classCast) {
            // If the mapping doesn't work, it'll error
            Logger.getGlobal().warning(classCast.getMessage());
        }

        // This also threw errors in testing, so it remains
        try {
            for (String key : permissionData.keySet()) {
                permissions.put(key, permissionData.getString(key));
            }
        } catch (ClassCastException classCast) {
            // If the mapping doesn't work, it'll error
            Logger.getGlobal().warning(classCast.getMessage());
        }
    }

    /**
     * Remaps SessionPermissions as a JsonObject
     * @return JsonObject Json representation of SessionPermissions
     */
    public JsonObject asJsonObject() {
        // Builder for overall JsonObject
        JsonObjectBuilder builder = Json.createObjectBuilder();

        // Adds the permissionId to overall JsonObject
        builder.add("permission_id", permissionId);

        // Adds the permission data to the overall JsonObject
        builder.add("permission_data", buildPermissionsJsonArray());

        return builder.build();
    }

    /**
     * Gets all Permissions that are allowed or conditionally allowed
     * @return ArrayList<String> list of allowed or conditionally allowed permissions
     */
    public ArrayList<String> getListOfPermissions() {
        ArrayList<String> listOfPermissions = new ArrayList<String>();

        for(String key : permissions.keySet()) {
            if(permissions.get(key).equals(PermissionValues.ALLOWED)
                    || permissions.get(key).equals(PermissionValues.IF_OWNER)) {
                listOfPermissions.add(key);
            }
        }

        return listOfPermissions;
    }

    /**
     * Gets the authority
     * This technically should break something, but it somehow doesn't
     * Documentation says it should throw null if using a custom authority, but it doesn't break if we
     * return null, so I'm not sure. Should probably relook at this to make sure
     *
     * @return String authority
     *
     */
    @Override
    public @Nullable String getAuthority() {
        return null;
    }

    // @TODO THIS BREAKS EVERYTHING RIGHT NOW CAUSE SESSIONPERMISSIONS IS NULL
    public String getPermission(String permissionToCheckFor) {
        try {
            String value = permissions.get(permissionToCheckFor);

            return PermissionValues.getStringAsPermissionValue(value);
        } catch (NullPointerException nullValue) {
            return PermissionValues.DENIED;
        }
    }

    protected JsonObject buildPermissionsJsonArray() {
        JsonObjectBuilder permissionBuilder = Json.createObjectBuilder();

        for(String key : permissions.keySet()) {
            permissionBuilder.add(key, permissions.get(key));
        }

        return permissionBuilder.build();
    }

    public int getPermissionId() {
        return permissionId;
    }
}
