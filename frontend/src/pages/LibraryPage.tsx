import { useState } from "react";
import type { GameStatus, Platform } from "../api/types";
import { useLibraryEntries, useLibraryStats } from "../api/useLibrary";
import { StatsStrip } from "../components/StatsStrip";
import { LibraryEntryCard } from "../components/LibraryEntryCard";
import { AddGameForm } from "../components/AddGameForm";

const STATUS_FILTERS: { label: string; value: GameStatus | undefined }[] = [
  { label: "All", value: undefined },
  { label: "Playing", value: "PLAYING" },
  { label: "Completed", value: "COMPLETED" },
  { label: "Backlog", value: "BACKLOG" },
  { label: "Wishlist", value: "WISHLIST" },
];

const PLATFORM_FILTERS: { label: string; value: Platform | undefined }[] = [
  { label: "All", value: undefined },
  { label: "PC", value: "PC" },
  { label: "PS5", value: "PS5" },
  { label: "Switch", value: "SWITCH" },
  { label: "Xbox", value: "XBOX" },
];

export function LibraryPage() {
  const [statusFilter, setStatusFilter] = useState<GameStatus | undefined>(undefined);
  const [platformFilter, setPlatformFilter] = useState<Platform | undefined>(undefined);

  const { data: stats } = useLibraryStats();
  const { data: entries, isLoading } = useLibraryEntries(statusFilter, platformFilter);

  return (
    <div className="library-page">
      <StatsStrip stats={stats} />

      <AddGameForm />

      <div className="filter-row">
        <div className="pill-group">
          {STATUS_FILTERS.map((f) => (
            <button
              key={f.label}
              className={`pill ${statusFilter === f.value ? "pill-active" : ""}`}
              onClick={() => setStatusFilter(f.value)}
            >
              {f.label}
            </button>
          ))}
        </div>
        <div className="pill-group">
          {PLATFORM_FILTERS.map((f) => (
            <button
              key={f.label}
              className={`pill ${platformFilter === f.value ? "pill-active" : ""}`}
              onClick={() => setPlatformFilter(f.value)}
            >
              {f.label}
            </button>
          ))}
        </div>
      </div>

      {isLoading && <div className="muted">Loading your library…</div>}

      {!isLoading && entries?.length === 0 && (
        <div className="empty-state">No games match these filters yet.</div>
      )}

      <div className="game-grid">
        {entries?.map((entry) => (
          <LibraryEntryCard key={entry.id} entry={entry} />
        ))}
      </div>
    </div>
  );
}
