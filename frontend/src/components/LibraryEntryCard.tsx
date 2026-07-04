import { Link } from "react-router-dom";
import type { GameStatus, LibraryEntryResponse } from "../api/types";
import { useDeleteLibraryEntry, useUpdateLibraryEntry } from "../api/useLibrary";

const STATUS_OPTIONS: GameStatus[] = ["PLAYING", "COMPLETED", "BACKLOG", "WISHLIST"];

export function LibraryEntryCard({ entry }: { entry: LibraryEntryResponse }) {
  const updateEntry = useUpdateLibraryEntry();
  const deleteEntry = useDeleteLibraryEntry();

  return (
    <div className="game-card">
      <Link to={`/games/${entry.gameId}`} className="game-cover">
        {entry.gameCoverUrl ? <img src={entry.gameCoverUrl} alt={entry.gameTitle} /> : <span>[ COVER ]</span>}
        <span className="platform-badge">{entry.platform}</span>
      </Link>
      <div className="game-card-body">
        <div className="game-title">{entry.gameTitle}</div>

        <select
          className="status-select"
          value={entry.status}
          onChange={(e) => updateEntry.mutate({ id: entry.id, request: { status: e.target.value as GameStatus } })}
        >
          {STATUS_OPTIONS.map((status) => (
            <option key={status} value={status}>
              {status}
            </option>
          ))}
        </select>

        <div className="game-card-row">
          <label>
            Hours
            <input
              type="number"
              min={0}
              defaultValue={entry.hoursPlayed}
              onBlur={(e) => {
                const value = Number(e.target.value);
                if (value !== entry.hoursPlayed) {
                  updateEntry.mutate({ id: entry.id, request: { hoursPlayed: value } });
                }
              }}
            />
          </label>
          <label>
            Rating
            <input
              type="number"
              min={0}
              max={5}
              defaultValue={entry.rating ?? ""}
              onBlur={(e) => {
                const value = e.target.value === "" ? undefined : Number(e.target.value);
                if (value !== undefined && value !== entry.rating) {
                  updateEntry.mutate({ id: entry.id, request: { rating: value } });
                }
              }}
            />
          </label>
        </div>

        <button className="btn-ghost btn-danger" onClick={() => deleteEntry.mutate(entry.id)}>
          Remove
        </button>
      </div>
    </div>
  );
}
