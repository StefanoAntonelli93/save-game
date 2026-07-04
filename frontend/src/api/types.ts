export type GameStatus = "PLAYING" | "COMPLETED" | "BACKLOG" | "WISHLIST";
export type Platform = "PC" | "PS5" | "SWITCH" | "XBOX";
export type Provider = "STEAM" | "XBOX" | "PSN";
export type AccountStatus = "ACTIVE" | "EXPIRED" | "ERROR";
export type TrophyTier = "PLATINUM" | "GOLD" | "SILVER" | "BRONZE";

export interface GameResponse {
  id: string;
  title: string;
  coverUrl: string | null;
  releaseDate: string | null;
  trailerUrl: string | null;
  summary: string | null;
  genres: string[];
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface LibraryEntryResponse {
  id: string;
  gameId: string;
  gameTitle: string;
  gameCoverUrl: string | null;
  platform: Platform;
  status: GameStatus;
  hoursPlayed: number;
  rating: number | null;
  addedAt: string;
  updatedAt: string;
}

export interface CreateLibraryEntryRequest {
  gameId: string;
  platform: Platform;
  status: GameStatus;
}

export interface UpdateLibraryEntryRequest {
  status?: GameStatus;
  hoursPlayed?: number;
  rating?: number;
}

export interface LibraryStatsResponse {
  playing: number;
  completed: number;
  backlog: number;
  wishlist: number;
  totalHoursPlayed: number;
}

export interface LinkedAccountResponse {
  id: string;
  provider: Provider;
  externalId: string;
  status: AccountStatus;
  lastSyncedAt: string | null;
}

export interface LinkAccountRequest {
  externalId: string;
  credentials: string;
}

export interface TrophyResponse {
  id: string;
  provider: Provider;
  name: string;
  description: string | null;
  tier: TrophyTier | null;
  iconUrl: string | null;
  unlocked: boolean;
  unlockedAt: string | null;
}
