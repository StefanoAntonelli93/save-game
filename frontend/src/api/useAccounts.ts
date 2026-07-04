import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useApiClient } from "./useApiClient";
import type { LinkAccountRequest, LinkedAccountResponse, Provider } from "./types";

const accountsKey = ["accounts"] as const;

export function useLinkedAccounts() {
  const api = useApiClient();
  return useQuery({
    queryKey: accountsKey,
    queryFn: () => api.get<LinkedAccountResponse[]>("/api/accounts"),
  });
}

export function useLinkAccount() {
  const api = useApiClient();
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ provider, request }: { provider: Provider; request: LinkAccountRequest }) =>
      api.post<LinkedAccountResponse>(`/api/accounts/${provider}/link`, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: accountsKey }),
  });
}

export function useUnlinkAccount() {
  const api = useApiClient();
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.delete(`/api/accounts/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: accountsKey }),
  });
}

export function useSyncAccount() {
  const api = useApiClient();
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.post(`/api/accounts/${id}/sync`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: accountsKey }),
  });
}
