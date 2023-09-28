-- Drop tables DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS conversation;
DROP TABLE IF EXISTS recipe_favorite;
DROP TABLE IF EXISTS ingredient;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS recipe;
DROP TABLE IF EXISTS account;

-- Create schema
CREATE TABLE account
(
    uid     text NOT NULL PRIMARY KEY,
    "name"  text,
    picture text,
    UNIQUE (uid)
);

CREATE TABLE recipe
(
    recipe_id      text    NOT NULL PRIMARY KEY,
    "public"       boolean NOT NULL,
    prep_time      int     NOT NULL,
    "name"         text    NOT NULL,
    img            text,
    favorite_count int CHECK (favorite_count >= 0) DEFAULT 0,
    uid            text    NOT NULL REFERENCES account (uid) ON DELETE CASCADE
);

CREATE TABLE step
(
    step_id     text NOT NULL PRIMARY KEY,
    sort        int  NOT NULL,
    description text NOT NULL,
    recipe_id   text NOT NULL REFERENCES recipe (recipe_id) ON DELETE CASCADE
);

CREATE TABLE ingredient
(
    ingredient_id text NOT NULL PRIMARY KEY,
    sort          int  NOT NULL,
    "name"        text NOT NULL,
    amount        int  NOT NULL,
    measure       text NOT NULL,
    recipe_id     text NOT NULL REFERENCES recipe (recipe_id) ON DELETE CASCADE
);

CREATE TABLE conversation
(
    conversation_id text NOT NULL,
    uid             text NOT NULL,
    notifications   int  NOT NULL CHECK (notifications >= 0) DEFAULT 0,
    PRIMARY KEY (conversation_id, uid)
);

CREATE TABLE message
(
    message_id      text      NOT NULL PRIMARY KEY,
    message_body    text      NOT NULL,
    uid             text      NOT NULL REFERENCES account (uid) ON DELETE CASCADE,
    conversation_id text      NOT NULL,
    created_at      timestamp NOT NULL DEFAULT now()
);

CREATE TABLE recipe_favorite
(
    id        serial NOT NULL PRIMARY KEY,
    recipe_id text   NOT NULL REFERENCES recipe (recipe_id) ON DELETE CASCADE,
    uid       text   NOT NULL REFERENCES account (uid) ON DELETE CASCADE
);