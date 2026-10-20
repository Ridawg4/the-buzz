This file will describe the format for both sets of Permission objects

The Permissions are split into two classes, as some values are not necessary at runtime, which wastes
memory for the likely situation those values are not needed

SessionPermission JSON representation:

The file must be laid out as specified in this documentation, or else the SessionPermissions class will
be unable to parse the file.

{
"permissions_id":{integer ID of the permission in the database},
"permission_data":{JsonObject Map of all the permission entries,
ex.) "CAN_READ":true, "CAN_WRITE":false, ...}
}

Permissions JSON Representation:

