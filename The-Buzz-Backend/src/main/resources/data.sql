USE development;

INSERT INTO users
VALUES (UUID_TO_BIN(UUID()),
        'testUser',
        'test',
        null,
        null,
        null,
        null,
        null,
        'a@a.c',
        null,
        null,
        null,
        null
    );

INSERT INTO users
VALUES (UUID_TO_BIN(UUID()),
        'testUser1',
        'test2',
        null,
        null,
        null,
        null,
        null,
        'a@b.c',
        null,
        null,
        null,
        null
       );