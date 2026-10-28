package com.teamwhite.thebuzz.model;

import com.teamwhite.thebuzz.Exceptions.InvalidPermissionsObjectException;
import com.teamwhite.thebuzz.Resources.PermissionValues;
import jakarta.json.Json;
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
    private int permissionId;
    private HashMap<String, String> permissions = new HashMap<String, String>();

    /**
     * Creates a SessionPermissions object with the formatted Json String of permissions
     * @param jsonStringToConvert String permissions to use
     */
    public SessionPermissions(String jsonStringToConvert) {
        JsonObject obj = Json.createReader(new StringReader(jsonStringToConvert.trim())).readObject();
        parseJsonAsPermissionsMap(obj);
    }

    /**
     * Creates a SessionPermissions object with the provided permissions
     * @param permissions JsonObject permissions
     */
    public SessionPermissions(JsonObject permissions) {
        parseJsonAsPermissionsMap(permissions);
    }

    /**
     * Creates a SessionPermissions object with the default permissions
     */
    public SessionPermissions() {
        // @TODO FIX WHEN PERMISSION GROUPS ARE ADDED TO USE SET DEFAULT PERMISSIONS
        File defaultUserPermissions = new File(DEFAULT_USER_PERMISSIONS_LOCATION);
        BufferedReader reader;
        StringBuilder permissionsJsonString = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(defaultUserPermissions));

            while(reader.ready()) {
                permissionsJsonString.append(reader.readLine());
            }
        } catch (IOException IOException) {
            Logger.getGlobal().warning("Default permissions not found, aborting");
        }
        // Maps the Json formatted String to a JsonObject
        JsonObject obj = Json.createReader(new StringReader(permissionsJsonString.toString())).readObject();
        parseJsonAsPermissionsMap(obj);
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
        // Builder for the actual permissions
        JsonObjectBuilder permissionBuilder = Json.createObjectBuilder();

        // Adds the permissionId to overall JsonObject
        builder.add("permission_id", permissionId);

        // Maps all the permissions into the permissions builder
        for(String key : permissions.keySet()) {
            permissionBuilder.add(key, permissions.get(key));
        }

        // Adds the permission data to the overall JsonObject
        builder.add("permission_data", permissionBuilder.build());

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

    public String getPermission(String permissionToCheckFor) {
        try {
            String value = permissions.get(permissionToCheckFor);

            return PermissionValues.getStringAsPermissionValue(value);
        } catch (NullPointerException nullValue) {
            return PermissionValues.DENIED;
        }
    }
}
