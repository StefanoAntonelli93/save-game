import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useApiClient } from "./useApiClient";
import type {
  CreateLibraryEntryRequest,
  GameStatus,
  LibraryEntryResponse,
  LibraryStatsResponse,
  Platform,
  UpdateLibraryEntryRequest,
} from "./types";

const libraryKeys = {
  all: ["library"] as const,
  list: (status?: GameStatus, platform?: Platform) => ["library", "list", status, platform] as const,
  stats: ["library", "stats"] as const,
};

export function useLibraryEntries(status?: GameStatus, platform?: Platform) {
  const api = useApiClient();
  return useQuery({
    queryKey: libraryKeys.list(status, platform),
    queryFn: () => {
      const params = new URLSearchParams();
      if (status) params.set("status", status);
      if (platform) params.set("platform", platform);
      const query = params.toString();
      return api.get<LibraryEntryResponse[]>(`/api/library${query ? `?${query}` : ""}`);
    },
  });
}

export function useLibraryStats() {
  const api = useApiClient();
  return useQuery({
    queryKey: libraryKeys.stats,
    queryFn: () => api.get<LibraryStatsResponse>("/api/library/stats"),
  });
}

export function useCreateLibraryEntry() {
  const api = useApiClient();
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateLibraryEntryRequest) => api.post<LibraryEntryResponse>("/api/library", request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: libraryKeys.all }),
  });
}

export function useUpdateLibraryEntry() {
  const api = useApiClient();
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, request }: { id: string; request: UpdateLibraryEntryRequest }) =>
      api.patch<LibraryEntryResponse>(`/api/library/${id}`, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: libraryKeys.all }),
  });
}

export function useDeleteLibraryEntry() {
  const api = useApiClient();
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.delete(`/api/library/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: libraryKeys.all }),
  });
}
