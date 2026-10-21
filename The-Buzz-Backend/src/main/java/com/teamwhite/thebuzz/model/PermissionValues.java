package com.teamwhite.thebuzz.model;

/**
 * All values that SessionPermission can have
 */
public class PermissionValues {
    public static final String ALLOWED = "allowed";
    /** The action is allowed, only if the user is an owner */
    public static final String IF_OWNER = "conditional-if-owner";
    public static final String DENIED = "denied";
}
