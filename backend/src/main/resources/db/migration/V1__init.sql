CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_sub  VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL,
    display_name  VARCHAR(255),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE games (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    igdb_id       BIGINT UNIQUE,
    title         VARCHAR(512) NOT NULL,
    cover_url     TEXT,
    release_date  DATE,
    trailer_url   TEXT,
    summary       TEXT,
    genres        TEXT[] NOT NULL DEFAULT '{}',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_games_title ON games USING gin (to_tsvector('simple', title));

CREATE TABLE game_platform_ids (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    game_id          UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    provider         VARCHAR(20) NOT NULL CHECK (provider IN ('STEAM', 'XBOX', 'PSN')),
    external_app_id  VARCHAR(255) NOT NULL,
    UNIQUE (provider, external_app_id)
);

CREATE TABLE library_entries (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    platform      VARCHAR(20) NOT NULL CHECK (platform IN ('PC', 'PS5', 'SWITCH', 'XBOX')),
    status        VARCHAR(20) NOT NULL CHECK (status IN ('PLAYING', 'COMPLETED', 'BACKLOG', 'WISHLIST')),
    hours_played  INTEGER NOT NULL DEFAULT 0,
    rating        SMALLINT CHECK (rating BETWEEN 0 AND 5),
    added_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, game_id, platform)
);

CREATE TABLE linked_accounts (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider         VARCHAR(20) NOT NULL CHECK (provider IN ('STEAM', 'XBOX', 'PSN')),
    external_id      VARCHAR(255) NOT NULL,
    credentials      TEXT NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'ERROR')),
    last_synced_at   TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, provider)
);

CREATE TABLE trophy_definitions (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    game_id              UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    provider             VARCHAR(20) NOT NULL CHECK (provider IN ('STEAM', 'XBOX', 'PSN')),
    external_trophy_id   VARCHAR(255) NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    description          TEXT,
    tier                 VARCHAR(20) CHECK (tier IN ('PLATINUM', 'GOLD', 'SILVER', 'BRONZE')),
    icon_url             TEXT,
    UNIQUE (game_id, provider, external_trophy_id)
);

CREATE TABLE user_trophies (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    trophy_definition_id   UUID NOT NULL REFERENCES trophy_definitions(id) ON DELETE CASCADE,
    unlocked_at            TIMESTAMPTZ,
    UNIQUE (user_id, trophy_definition_id)
);
