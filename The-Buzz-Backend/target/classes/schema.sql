# order modified from the schemas so the referenced keys are created before being referenced

# UUID converts to BINARY(16) and is converted back at request
# To generate a UUID to put in a table, when inserting into the table, use UUID_TO_BIN(UUID())
# UUID() will generate a new UUID, and UUID_TO_BIN() will convert that UUID to BINARY

CREATE DATABASE development;


USE development;

CREATE TABLE IF NOT EXISTS  users (
    user_id BINARY(16) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    oauth_key VARCHAR(255),
    join_date TIMESTAMP,
    user_desc VARCHAR(255),
    full_name VARCHAR(255),
    user_pfp_location VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    favorites JSON,
    volume_settings JSON,
    notification_settings JSON,
    permission_data JSON
);
CREATE TABLE IF NOT EXISTS  permissions (
    permission_id INTEGER PRIMARY KEY,
    is_admin BOOLEAN NOT NULL,
    is_moderator BOOLEAN NOT NULL,
    is_dj BOOLEAN NOT NULL,
    is_custom_permissions BOOLEAN NOT NULL,
    permissions JSON,
    created_by BINARY(16),
    created_at TIMESTAMP,
    permission_desc VARCHAR(255),

    FOREIGN KEY (created_by)
        REFERENCES users(user_id)
);
CREATE TABLE IF NOT EXISTS  djs (
    dj_id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    dj_name VARCHAR(255) NOT NULL,
    genre VARCHAR(255),
    dj_pfp_location VARCHAR(255),

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);
CREATE TABLE IF NOT EXISTS  shows (
    show_id BINARY(16) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    dj_id BINARY(16) NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    show_date DATE,

    FOREIGN KEY (dj_id)
        REFERENCES djs(dj_id)
);
CREATE TABLE IF NOT EXISTS account_moderation (
    user_id BINARY(16) NOT NULL,
    moderation_desc VARCHAR(255),
    date_applied TIMESTAMP,
    moderator_applied_action VARCHAR(255),
    moderation_severity VARCHAR(50),
    date_expires TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);
#self referencing itself allowing one comment to reply to another comment
CREATE TABLE IF NOT EXISTS  comments (
    comment_id INTEGER PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    post_id INTEGER NOT NULL,
    reply_id INTEGER,
    comment_content VARCHAR(1000) NOT NULL,
    last_updated TIMESTAMP,
    created_at TIMESTAMP NOT NULL,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id),

    FOREIGN KEY (reply_id)
        REFERENCES comments(comment_id)
);

CREATE USER "credentialsReader"@"localhost" IDENTIFIED BY "testingpassword";
CREATE USER "credentialsWriter"@"localhost" IDENTIFIED BY "testingpassword";

GRANT SELECT ON development.users TO 'credentialsReader'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON development.users TO 'credentialsWriter'@'localhost';
