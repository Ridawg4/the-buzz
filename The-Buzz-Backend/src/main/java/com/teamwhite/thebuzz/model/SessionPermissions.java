package com.teamwhite.thebuzz.model;

import com.teamwhite.thebuzz.Exceptions.InvalidPermissionsObjectException;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class SessionPermissions implements GrantedAuthority {
    private int permissionId;
    private HashMap<String, String> permissions = new HashMap<String, String>();

    public SessionPermissions(String jsonStringToConvert) {
        JsonObject obj = Json.createReader(new StringReader(jsonStringToConvert)).readObject();
        parseJsonAsPermissionsMap(obj);
    }

    public SessionPermissions(File pathToDefaultPermissionFile) {
        BufferedReader reader;
        String permissionsJsonString = "";
        try {
            reader = new BufferedReader(new FileReader(pathToDefaultPermissionFile));

            while(reader.ready()) {
                permissionsJsonString += reader.readLine();
            }
        } catch (IOException IOException) {
            Logger.getGlobal().warning("Default permissions not found, aborting");
        }
        parseJsonAsPermissionsMap(Json.createReader(new StringReader(permissionsJsonString)).readObject());
    }

    public SessionPermissions(JsonObject permissions) {
        parseJsonAsPermissionsMap(permissions);
    }

    public SessionPermissions() {}

    private void parseJsonAsPermissionsMap(JsonObject json) {
        if(!json.containsKey("permission_id")) {
            throw new InvalidPermissionsObjectException("Json mapping does not contain \"permission_id\"" +
                    " field");
        }
        if(!json.containsKey("permission_data")) {
            throw new InvalidPermissionsObjectException("Json mapping does not contain \"permission_data\"" +
                    " field");
        }
        JsonObject permissionData = null;
        try {
            permissionData = json.getJsonObject("permission_data");
            permissionId = json.getInt("permission_id");
        } catch (ClassCastException classCast) {
            Logger.getGlobal().warning(classCast.getMessage());
        }

        try {
            for (String key : permissionData.keySet()) {
                permissions.put(key, permissionData.getString(key));
            }
        } catch (ClassCastException classCast) {
            Logger.getGlobal().warning(classCast.getMessage());
        }
    }

    public JsonObject asJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonObjectBuilder permissionMapBuilder = Json.createObjectBuilder();
        builder.add("permission_id", permissionId);

        for(String key : permissions.keySet()) {
            permissionMapBuilder.add(key, permissions.get(key));
        }

        builder.add("permission_data", permissionMapBuilder.build());
        JsonObject obj = builder.build();
        Logger.getGlobal().info(obj.toString());
        return obj;
    }

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

    @Override
    public @Nullable String getAuthority() {
        return null;
    }
}
