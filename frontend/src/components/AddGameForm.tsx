import { useState } from "react";
import type { GameStatus, Platform } from "../api/types";
import { useGames } from "../api/useGames";
import { useCreateLibraryEntry } from "../api/useLibrary";

const PLATFORMS: Platform[] = ["PC", "PS5", "SWITCH", "XBOX"];
const STATUSES: GameStatus[] = ["PLAYING", "COMPLETED", "BACKLOG", "WISHLIST"];

export function AddGameForm() {
  const [search, setSearch] = useState("");
  const [platform, setPlatform] = useState<Platform>("PC");
  const [status, setStatus] = useState<GameStatus>("BACKLOG");

  const { data: results, isFetching } = useGames(search);
  const createEntry = useCreateLibraryEntry();

  return (
    <div className="add-game-form">
      <input
        className="search-input"
        placeholder="Search the catalog…"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <select value={platform} onChange={(e) => setPlatform(e.target.value as Platform)}>
        {PLATFORMS.map((p) => (
          <option key={p} value={p}>
            {p}
          </option>
        ))}
      </select>
      <select value={status} onChange={(e) => setStatus(e.target.value as GameStatus)}>
        {STATUSES.map((s) => (
          <option key={s} value={s}>
            {s}
          </option>
        ))}
      </select>

      {search && (
        <div className="search-results">
          {isFetching && <div className="muted">Searching…</div>}
          {results?.content.length === 0 && !isFetching && <div className="muted">No games found.</div>}
          {results?.content.map((game) => (
            <button
              key={game.id}
              className="search-result-row"
              onClick={() => createEntry.mutate({ gameId: game.id, platform, status })}
            >
              {game.title}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
