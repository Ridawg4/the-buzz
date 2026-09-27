--order modified from the schemas so the referenced keys are created before being referenced 

CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    oauth_key VARCHAR(255),
    join_date TIMESTAMP,
    user_desc VARCHAR(255),
    full_name VARCHAR(255),
    user_pfp_location VARCHAR(255),
    email VARCHAR(255),
    favorites JSONB,
    volume_settings JSONB,
    notification_settings JSONB,
    permission_data JSONB
);
CREATE TABLE permissions (
    permission_id INTEGER PRIMARY KEY,
    is_admin BOOLEAN NOT NULL,
    is_moderator BOOLEAN NOT NULL,
    is_dj BOOLEAN NOT NULL,
    is_custom_permissions BOOLEAN NOT NULL,
    permissions JSONB,
    created_by UUID,
    created_at TIMESTAMP,
    permission_desc VARCHAR(255),

    FOREIGN KEY (created_by)
        REFERENCES users(user_id)
);
CREATE TABLE djs (
    dj_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    dj_name VARCHAR(255) NOT NULL,
    genre VARCHAR(255),
    dj_pfp_location VARCHAR(255),

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);
CREATE TABLE shows (
    show_id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    dj_id UUID NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    show_date DATE,

    FOREIGN KEY (dj_id)
        REFERENCES djs(dj_id)
);
CREATE TABLE account_moderation (
    user_id UUID NOT NULL,
    moderation_desc VARCHAR(255),
    date_applied TIMESTAMP,
    moderator_applied_action VARCHAR(255),
    moderation_severity VARCHAR(50),
    date_expires TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);
CREATE TABLE comments (
    comment_id INTEGER PRIMARY KEY,
    user_id UUID NOT NULL,
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
