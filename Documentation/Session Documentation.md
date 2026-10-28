# Sessions

Our sessions are modified version of the default Spring Sessions, which can cause weird behavior

Our sessions store the following data:
- Session ID: UUID 
  - The ID of the session
- Created at: Timestamp 
  - The time the session was first created
- Last Accessed at: Timestamp 
  - The last time the session was used
- Expiry Duration: Duration 
  - The duration of time the session must be idle before it's deleted
- Permissions: SessionPermissions 
  - The permissions that the session has
- Attributes: JsonObject
  - The attributes Spring Session expects
  > Spring Boot stores its redirects and SecurityContextImpl here, which allows for the sessions to persist across sessions and for redirects when attempting to access a resource that needs authentication

Our sessions also have external storage to track details about user sessions:
- Session ID: UUID 
  - The session ID
- OwnerID: UUID 
  - The owner of the session
- NeedsRenewal: Boolean
  - Notifies the system if a session needs to be renewed after a change