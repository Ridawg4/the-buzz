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
}
