# Save-State

Track the games you've played, the ones you're playing, and the ones you want to play — with real trophy/achievement sync from Steam, Xbox and PlayStation, and a game catalog backed by IGDB.

## Architecture

Modular monolith: one deployable Spring Boot backend (split into modules by responsibility), a separate frontend SPA, Keycloak for identity, Postgres for storage.

```
┌─────────────┐       ┌──────────────────┐       ┌─────────────────┐
│  Frontend    │──────▶│  Keycloak         │       │  External APIs   │
│  (SPA)       │◀──────│  (auth: local +   │       │  IGDB / Steam /  │
└──────┬───────┘  JWT  │   Google IdP)     │       │  OpenXBL / PSN   │
       │                └──────────────────┘       └────────▲────────┘
       │ Bearer JWT                                          │
       ▼                                                      │
┌─────────────────────────────────────────────────────────┐  │
│  Spring Boot backend (Resource Server)                    │  │
│  ┌───────────┐ ┌──────────┐ ┌────────────┐ ┌───────────┐ │  │
│  │ Catalog   │ │ Library  │ │ Accounts   │ │ Trophies  │─┼──┘
│  │ module    │ │ module   │ │ module     │ │ module    │ │
│  └───────────┘ └──────────┘ └────────────┘ └───────────┘ │
└──────────────────────────┬────────────────────────────────┘
                            ▼
                     ┌─────────────┐
                     │  PostgreSQL  │
                     └─────────────┘
```

### Modules

- **Identity** — delegated entirely to Keycloak (local username/password + Google as a federated IdP). The backend never handles passwords; it only validates JWTs issued by Keycloak and JIT-provisions a `User` row from the token's `sub` claim on first request.
- **Catalog** — game metadata (title, cover, release date, official trailer, genres) synced from [IGDB](https://api-docs.igdb.com/) and cached locally in Postgres so browsing/search doesn't hit external rate limits.
- **Library** — the core feature: each user's per-game entries (status, platform, hours played, rating).
- **Accounts** — per-user linked platform accounts (Steam, Xbox, PSN) used only to authorize trophy sync. Credentials/tokens are encrypted at rest.
- **Trophies** — trophy/achievement definitions per game+provider, plus each user's unlock progress, populated by scheduled sync jobs.

### Trophy provider feasibility

| Provider | API | Notes |
|---|---|---|
| **Steam** | ✅ Official (`ISteamUserStats`) | Key + SteamID64, reliable |
| **Xbox** | ⚠️ Unofficial gateway ([OpenXBL](https://xbl.io)) | No accessible first-party API for third-party apps without partner approval |
| **PlayStation** | ⚠️ Experimental / unofficial | No public API; relies on a reverse-engineered NPSSO-token library. Flagged as best-effort — can break without notice |
| **Nintendo** | ❌ Not supported | Switch has no platform-wide trophy/achievement system to sync |

Each provider is implemented behind a single `TrophyProviderClient` interface, so reliability differences stay isolated per adapter instead of leaking into the rest of the app.

### Entity model (core tables)

- `users` — id, keycloak_sub, email, display_name
- `games` — id, igdb_id, title, cover_url, release_date, trailer_url, summary, genres
- `game_platform_ids` — maps a catalog game to its Steam/Xbox/PSN app ID
- `library_entries` — user_id, game_id, platform, status (playing/completed/backlog/wishlist), hours_played, rating
- `linked_accounts` — user_id, provider, external_id, encrypted credentials, sync status
- `trophy_definitions` — game_id, provider, external_trophy_id, name, description, tier, icon_url
- `user_trophies` — user_id, trophy_definition_id, unlocked_at

### API sketch

```
GET    /api/games?search=&platform=&page=       search local catalog cache (IGDB-backed)
GET    /api/games/{id}
GET    /api/games/{id}/trophies                  trophy defs + current user's unlock state

GET    /api/library?status=&platform=            current user's entries
POST   /api/library            {gameId, platform, status}
PATCH  /api/library/{id}       {status, hoursPlayed, rating}
DELETE /api/library/{id}
GET    /api/library/stats                         counts per status + total hours

GET    /api/accounts                               linked platform accounts
POST   /api/accounts/{provider}/link               start OAuth/token flow
DELETE /api/accounts/{id}
POST   /api/accounts/{id}/sync                     trigger trophy sync now
```

Auth is offloaded to Keycloak — the frontend runs Authorization Code + PKCE directly against it; the backend only validates the resulting Bearer JWT.

### Tech stack

- **Backend**: Spring Boot 3.x, Java 21, Spring Data JPA + PostgreSQL, Flyway, Spring Security (OAuth2 Resource Server), Spring WebClient + Resilience4j for external API calls, MapStruct, Testcontainers
- **Auth**: Keycloak (local password + Google federated login)
- **Frontend**: React 19 + TypeScript + Vite, TanStack Query, react-router-dom, react-oidc-context for Keycloak auth
- **Deployment**: docker-compose (Postgres + Keycloak + backend, frontend served separately), single VM to start

## Repository structure

```
/backend    Spring Boot REST API
/frontend   Client SPA
/design     Original design mockups (interactive HTML prototypes + screenshots)
```

## Status

Architecture designed. Backend and frontend skeletons scaffolded — see [backend/README.md](backend/README.md) and [frontend/README.md](frontend/README.md) for what's real vs. stubbed in each. Keycloak realm/client setup and real provider credentials (IGDB, Steam, etc.) still need to be configured manually.
