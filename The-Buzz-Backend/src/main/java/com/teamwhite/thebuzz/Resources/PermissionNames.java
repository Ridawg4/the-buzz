package com.teamwhite.thebuzz.Resources;

/** Describes all the permission names and their possible values
 * For using these with @PreAuthorize or @PostAuthorize, you must turn the values into constants
 *
 */
public class PermissionNames {
    // ALL USER PERMISSIONS

    /** Does the user have the ability to add favorites? | ALLOWED */
    public static final String CAN_ADD_FAVORITES = "CAN_ADD_FAVORITES";
    /** Does the user have the ability to remove favorites? | ALLOWED */
    public static final String CAN_REMOVE_FAVORITES = "CAN_REMOVE_FAVORITES";
    /** Does the user have the ability to add favorites? | ALLOWED */
    public static final String CAN_LIKE_SHOWS = "CAN_LIKE_SHOWS";
    /** Does the user have the ability to like comments? | ALLOWED */
    public static final String CAN_LIKE_COMMENTS = "CAN_LIKE_COMMENTS";
    /** Does the user have the ability to post comments? | ALLOWED, DENIED */
    public static final String CAN_POST_COMMENTS = "CAN_POST_COMMENTS";
    /** Does the user have the ability to like comments? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_DELETE_COMMENTS = "CAN_DELETE_COMMENTS";
    /** Does the user have the ability to like comments? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_SEE_ACCOUNT_INFO = "CAN_SEE_ACCOUNT_INFO";
    /** Does the user have the ability to like comments? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_MODIFY_ACCOUNT_INFO = "CAN_MODIFY_ACCOUNT_INFO";
    /** Does the user have the ability to like comments? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_DELETE_ACCOUNT = "CAN_DELETE_ACCOUNT";

    // ALL DJ PERMISSIONS

    /** Does the user have the ability to see DJ specific pages? | ALLOWED, DENIED */
    public static final String CAN_SEE_DJ_SYSTEM_PANELS = "CAN_SEE_DJ_SYSTEM_PANELS";
    /** Does the user have the ability to create a DJ profile? | CONDITIONAL-IF-NOT-EXCEEDED-LIMIT, DENIED */
    public static final String CAN_CREATE_DJ_PROFILE = "CAN_CREATE_DJ_PROFILE";
    /** Does the user have the ability to modify a DJ profile? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_MODIFY_DJ_PROFILE = "CAN_MODIFY_DJ_PROFILE";
    /** Does the user have the ability to delete a DJ profile? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_DELETE_DJ_PROFILE = "CAN_DELETE_DJ_PROFILE";
    /** Does the user have the ability to create a show listing? | CONDITIONAL-IF-NOT-EXCEEDED-LIMIT, DENIED */
    public static final String CAN_CREATE_SHOW_LISTING = "CAN_CREATE_SHOW_LISTING";
    /** Does the user have the ability to modify a show listing? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_MODIFY_SHOW_LISTING = "CAN_MODIFY_SHOW_LISTING";
    /** Does the user have the ability to delete a show listing? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_DELETE_SHOW_LISTING = "CAN_DELETE_SHOW_LISTING";
    /** Does the user have the ability to upload a prerecorded audio episode? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_UPLOAD_PRERECORDED_SHOW_AUDIO = "CAN_UPLOAD_PRERECORDED_SHOW_AUDIO";
    /** Does the user have the ability to delete a prerecorded audio episode? | CONDITIONAL-IF-OWNER, DENIED */
    public static final String CAN_DELETE_PRERECORDED_SHOW_AUDIO = "CAN_DELETE_PRERECORDED_SHOW_AUDIO";

    // ALL MODERATION PERMISSIONS

    /** Does the user have the ability to see all system pages? | ALLOWED, DENIED */
    public static final String CAN_SEE_ALL_SYSTEM_PANELS = "CAN_SEE_ALL_SYSTEM_PANELS";
    /** Does the user have the ability to create any DJ profile? | ALLOWED, DENIED */
    public static final String CAN_CREATE_ANY_DJ_PROFILE = "CAN_CREATE_ANY_DJ_PROFILE";
    /** Does the user have the ability to modify any DJ profiles? | ALLOWED, DENIED */
    public static final String CAN_MODIFY_ANY_DJ_PROFILE = "CAN_MODIFY_ANY_DJ_PROFILE";
    /** Does the user have the ability to delete any DJ profile? | ALLOWED, DENIED */
    public static final String CAN_DELETE_ANY_DJ_PROFILE = "CAN_DELETE_ANY_DJ_PROFILE";
    /** Does the user have the ability to delete any comment? | ALLOWED, DENIED */
    public static final String CAN_DELETE_ANY_COMMENT = "CAN_DELETE_ANY_COMMENT";
    /** Does the user have the ability to create any show listing? | ALLOWED, DENIED */
    public static final String CAN_CREATE_ANY_SHOW_LISITNG = "CAN_CREATE_ANY_SHOW_LISITNG";
    /** Does the user have the ability to modify any show listing? | ALLOWED, DENIED */
    public static final String CAN_MODIFY_ANY_SHOW_LISTING = "CAN_MODIFY_ANY_SHOW_LISTING";
    /** Does the user have the ability to delete any show listing? | ALLOWED, DENIED */
    public static final String CAN_DELETE_ANY_SHOW_LISTING = "CAN_DELETE_ANY_SHOW_LISTING";
    /** Does the user have the ability to upload a prerecorded audio episode? | ALLOWED, DENIED */
    public static final String CAN_UPLOAD_ANY_PRERECORDED_SHOW_AUDIO = "CAN_UPLOAD_ANY_PRERECORDED_SHOW_AUDIO";
    /** Does the user have the ability to delete a prerecorded audio episode? | ALLOWED, DENIED */
    public static final String CAN_DELETE_ANY_PRERECORDED_SHOW_AUDIO = "CAN_DELETE_ANY_PRERECORDED_SHOW_AUDIO";
    /** Does the user have the ability to modify show schedule visibility? | ALLOWED, DENIED */
    public static final String CAN_MODIFY_SHOW_SCHEDULE_VISIBILITY = "CAN_MODIFY_SHOW_SCHEDULE_VISIBILITY";
    /** Does the user have the ability to view account action logs? | ALLOWED, DENIED */
    public static final String CAN_VIEW_ACCOUNT_ACTION_LOGS = "CAN_VIEW_ACCOUNT_ACTION_LOGS";
    /** Does the user have the ability to create an account action entry? | ALLOWED, DENIED */
    public static final String CAN_CREATE_ACCOUNT_ACTION_ENTRY = "CAN_CREATE_ACCOUNT_ACTION_ENTRY";
    /** Does the user have the ability to modify an account action entry? | ALLOWED, DENIED */
    public static final String CAN_MODIFY_ACCOUNT_ACTION_ENTRY = "CAN_MODIFY_ACCOUNT_ACTION_ENTRY";
    /** Does the user have the ability to delete an account action entry? | ALLOWED, DENIED */
    public static final String CAN_DELETE_ACCOUNT_ACTION_ENTRY = "CAN_DELETE_ACCOUNT_ACTION_ENTRY";
    /** Does the user have the ability to view any account information? | ALLOWED, DENIED */
    public static final String CAN_VIEW_ANY_ACCOUNT_INFO = "CAN_VIEW_ANY_ACCOUNT_INFO";
    /** Does the user have the ability to modify any account information? | ALLOWED, DENIED */
    public static final String CAN_MODIFY_ANY_ACCOUNT_INFO = "CAN_MODIFY_ANY_ACCOUNT_INFO";
    /** Does the user have the ability to delete any account information? | ALLOWED, DENIED */
    public static final String CAN_DELETE_ANY_ACCOUNT_INFO = "CAN_DELETE_ANY_ACCOUNT_INFO";
    /** Does the user have the ability to create permission groups? | ALLOWED, DENIED */
    public static final String CAN_CREATE_PERMISSION_GROUPS = "CAN_CREATE_PERMISSION_GROUPS";
    /** Does the user have the ability to modify permission groups? | ALLOWED, DENIED */
    public static final String CAN_MODIFY_PERMISSION_GROUPS = "CAN_MODIFY_PERMISSION_GROUPS";
    /** Does the user have the ability to delete permission groups? | ALLOWED, DENIED */
    public static final String CAN_DELETE_PERMISSION_GROUPS = "CAN_DELETE_PERMISSION_GROUPS";
    /** Does the user have the ability to set the default permission groups? | ALLOWED, DENIED */
    public static final String CAN_SET_DEFAULT_PERMISSION_GROUP = "CAN_SET_DEFAULT_PERMISSION_GROUP";
    /** Does the user have the ability to view the server runtime logs? | ALLOWED, DENIED */
    public static final String CAN_VIEW_SERVER_RUNTIME_LOGS = "CAN_VIEW_SERVER_RUNTIME_LOGS";
    /** Does the user have the ability to view the system usage statistics? | ALLOWED, DENIED */
    public static final String CAN_VIEW_SYSTEM_USAGE_STATISTICS = "CAN_VIEW_SYSTEM_USAGE_STATISTICS";
    /** Does the user have the ability to download the system usage statistics? | ALLOWED, DENIED */
    public static final String CAN_DOWNLOAD_SYSTEM_USAGE_STATISTICS = "CAN_DOWNLOAD_SYSTEM_USAGE_STATISTICS";
    /** Does the user have the ability to upload live stream segments? | ALLOWED, DENIED */
    public static final String CAN_UPLOAD_LIVE_STREAM_SEGMENTS = "CAN_UPLOAD_LIVE_STREAM_SEGMENTS";
    /** Does the user have the ability to set the live stream to record? | ALLOWED, DENIED */
    public static final String CAN_SET_LIVE_STREAM_RECORDING = "CAN_SET_LIVE_STREAM_RECORDING";
    /** Does the user have the ability to disable the live stream? | ALLOWED, DENIED */
    public static final String CAN_DISABLE_LIVE_STREAM = "CAN_DISABLE_LIVE_STREAM";
}
