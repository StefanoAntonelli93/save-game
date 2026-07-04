import type { LibraryStatsResponse } from "../api/types";

export function StatsStrip({ stats }: { stats: LibraryStatsResponse | undefined }) {
  const tiles: { label: string; value: number; className: string }[] = [
    { label: "PLAYING", value: stats?.playing ?? 0, className: "stat-playing" },
    { label: "COMPLETED", value: stats?.completed ?? 0, className: "stat-completed" },
    { label: "BACKLOG", value: stats?.backlog ?? 0, className: "stat-backlog" },
    { label: "WISHLIST", value: stats?.wishlist ?? 0, className: "stat-wishlist" },
    { label: "HOURS", value: stats?.totalHoursPlayed ?? 0, className: "stat-hours" },
  ];

  return (
    <div className="stats-strip">
      {tiles.map((tile) => (
        <div className="stat-tile" key={tile.label}>
          <div className="stat-label">{tile.label}</div>
          <div className={`stat-value ${tile.className}`}>{tile.value.toLocaleString()}</div>
        </div>
      ))}
    </div>
  );
}
