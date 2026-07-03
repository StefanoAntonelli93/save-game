# Save-State — Backend

Spring Boot 3.5 / Java 21 REST API. See the [root README](../README.md) for the full architecture.

## Modules

- `identity` — JWT-driven, JIT-provisions a `User` from the Keycloak token on first request
- `catalog` — local `games` cache + IGDB client (`catalog/igdb`, untested against live credentials yet)
- `library` — per-user game entries (status/platform/hours/rating) — fully implemented
- `accounts` — linked Steam/Xbox/PSN accounts used for trophy sync
- `trophies` — trophy definitions/unlocks + provider adapters (`trophies/sync`)

## What's real vs. stubbed

- **Library CRUD, stats, catalog search/read** — fully working against Postgres.
- **Steam trophy sync** — real implementation against the official `ISteamUserStats` API.
- **Xbox / PSN trophy sync** — stubs that throw `UnsupportedOperationException`. Xbox needs an OpenXBL integration, PSN needs the unofficial NPSSO-based client. Both are flagged experimental in the architecture doc — implement when you're ready to accept their reliability tradeoffs.
- **IGDB catalog sync** — implemented (Twitch OAuth2 client-credentials + Apicalypse query) but not yet wired into `GameService.search()`'s cache-miss path, and untested against real credentials.
- **Credential encryption** — `linked_accounts.credentials` is stored as plain text for now. Encrypt at rest (e.g. via a KMS or `pgcrypto`) before storing real tokens.

## Running locally

Requires Java 21, Docker (for Postgres/Keycloak and for the Testcontainers-based test).

```bash
docker compose up postgres keycloak   # from the repo root
./mvnw spring-boot:run
```

The default `application.yml` already points at `localhost:5432` (Postgres) and `localhost:8081` (Keycloak) — running the backend on the host against the two containers above needs no extra config.

> **Keycloak hostname caveat**: if you instead run the `backend` service *inside* docker-compose too, the JWT `iss` claim your browser gets from Keycloak (via `localhost:8081`) won't match what the backend expects when it reaches Keycloak over the internal `keycloak:8080` Docker DNS name. Either keep the backend on the host during development (as above), or configure Keycloak's `KC_HOSTNAME` so both paths resolve to the same issuer URL.

You'll need a Keycloak realm named `savestate` with a client for the frontend and Google configured as an identity provider — not scripted yet.

### Required environment variables (see `application.yml` for defaults)

| Variable | Purpose |
|---|---|
| `KEYCLOAK_ISSUER_URI` | Realm issuer URL, used to validate JWTs |
| `IGDB_CLIENT_ID` / `IGDB_CLIENT_SECRET` | Twitch app credentials for IGDB |
| `STEAM_API_KEY` | Steam Web API key |
| `OPENXBL_API_KEY` | OpenXBL key, once the Xbox client is implemented |
| `TROPHY_SYNC_ENABLED` | Off by default — flip to `true` once provider credentials are in place |

## Tests

```bash
./mvnw test
```

`LibraryControllerIT` spins up a real Postgres via Testcontainers and drives the full HTTP stack (MockMvc + a mocked JWT principal) — no live Keycloak needed to run it.
