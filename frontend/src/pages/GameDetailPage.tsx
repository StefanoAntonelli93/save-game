import { useParams } from "react-router-dom";
import { useGame, useGameTrophies } from "../api/useGames";

export function GameDetailPage() {
  const { gameId } = useParams<{ gameId: string }>();
  const { data: game } = useGame(gameId);
  const { data: trophies, isLoading } = useGameTrophies(gameId);

  return (
    <div className="game-detail-page">
      <h1>{game?.title ?? "Loading…"}</h1>
      {game?.summary && <p className="muted">{game.summary}</p>}
      {game?.trailerUrl && (
        <a className="btn-ghost" href={game.trailerUrl} target="_blank" rel="noreferrer">
          Watch trailer
        </a>
      )}

      <h2>Trophies</h2>
      {isLoading && <div className="muted">Loading trophies…</div>}
      {!isLoading && trophies?.length === 0 && (
        <div className="muted">
          No trophy data yet — link a platform account and sync from the Accounts page.
        </div>
      )}
      <div className="trophy-list">
        {trophies?.map((trophy) => (
          <div key={trophy.id} className={`trophy-row ${trophy.unlocked ? "trophy-unlocked" : "trophy-locked"}`}>
            <div className="trophy-tier">{trophy.tier ?? trophy.provider}</div>
            <div>
              <div className="trophy-name">{trophy.name}</div>
              {trophy.description && <div className="muted">{trophy.description}</div>}
            </div>
            <div className="trophy-status">{trophy.unlocked ? "Unlocked" : "Locked"}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
