import { useQuery } from "@tanstack/react-query";
import { useApiClient } from "./useApiClient";
import type { GameResponse, Page, TrophyResponse } from "./types";

export function useGames(search: string) {
  const api = useApiClient();
  return useQuery({
    queryKey: ["games", "search", search],
    queryFn: () => {
      const params = new URLSearchParams();
      if (search) params.set("search", search);
      return api.get<Page<GameResponse>>(`/api/games?${params.toString()}`);
    },
  });
}

export function useGame(gameId: string | undefined) {
  const api = useApiClient();
  return useQuery({
    queryKey: ["games", gameId],
    queryFn: () => api.get<GameResponse>(`/api/games/${gameId}`),
    enabled: !!gameId,
  });
}

export function useGameTrophies(gameId: string | undefined) {
  const api = useApiClient();
  return useQuery({
    queryKey: ["games", gameId, "trophies"],
    queryFn: () => api.get<TrophyResponse[]>(`/api/games/${gameId}/trophies`),
    enabled: !!gameId,
  });
}
