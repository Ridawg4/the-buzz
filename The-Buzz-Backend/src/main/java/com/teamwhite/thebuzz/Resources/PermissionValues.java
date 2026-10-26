package com.teamwhite.thebuzz.Resources;

/**
 * All values that SessionPermission can possibly have
 */
public class PermissionValues {
    public static final String ALLOWED = "allowed";
    /** The action is allowed, only if the user is an owner */
    public static final String IF_OWNER = "conditional-if-owner";
    /** The action is allowed, only if the user is within their creation limit */
    public static final String IF_NOT_EXCEEDED_LIMIT = "conditional-if-not-exceeded-limit";
    public static final String DENIED = "denied";

    public static String getStringAsPermissionValue(String permission) {
        switch(permission) {
            case ALLOWED:
                return ALLOWED;
            case IF_OWNER:
                return IF_OWNER;
            case IF_NOT_EXCEEDED_LIMIT:
                return IF_NOT_EXCEEDED_LIMIT;
            default:
                return DENIED;
        }
    }
}
