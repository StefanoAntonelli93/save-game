# Save-State — Frontend

React 19 + TypeScript + Vite. See the [root README](../README.md) for the full architecture.

## Stack

- **Routing**: react-router-dom
- **Server state**: TanStack Query (all API calls go through hooks in `src/api/`)
- **Auth**: `react-oidc-context` / `oidc-client-ts` — Authorization Code + PKCE directly against Keycloak, no backend involvement in the login flow
- **Styling**: plain CSS (`src/index.css`), carrying over the dark palette from the [design mockups](../design)

## Structure

```
src/
  api/         typed DTOs + fetch client + React Query hooks, one file per backend resource
  auth/        OIDC config + RequireAuth gate
  components/  shared UI pieces (Layout, StatsStrip, GameCard, AddGameForm)
  pages/       LibraryPage (full CRUD), GameDetailPage (trophies), AccountsPage (linked accounts)
```

## What's real vs. stubbed

- **Library page** — fully wired: list/filter by status+platform, stats, inline status/hours/rating edits, catalog search to add a game, delete.
- **Accounts page** — fully wired to the accounts API (link/unlink/sync). Steam is the only provider with a working backend sync; Xbox/PSN are labeled in the UI as not implemented / experimental, matching the backend.
- **Game detail page** — fetches and renders trophies; will show an empty state until a linked account has synced.

## Running locally

```bash
cp .env.example .env   # adjust if your backend/Keycloak aren't on the default ports
npm install
npm run dev
```

Requires the backend running (see [`../backend/README.md`](../backend/README.md)) and a Keycloak realm with a public client matching `VITE_KEYCLOAK_CLIENT_ID` (redirect URI = the Vite dev server origin, e.g. `http://localhost:5173`). Not scripted yet — set up manually in the Keycloak admin console.

```bash
npm run build   # type-check (tsc -b) + production build
npm run lint    # oxlint
```
