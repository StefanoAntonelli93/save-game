package com.savestate.backend.library.dto;

import com.savestate.backend.library.GameStatus;
import com.savestate.backend.library.Platform;

import java.time.Instant;
import java.util.UUID;

public record LibraryEntryResponse(
    UUID id,
    UUID gameId,
    String gameTitle,
    String gameCoverUrl,
    Platform platform,
    GameStatus status,
    Integer hoursPlayed,
    Integer rating,
    Instant addedAt,
    Instant updatedAt
) {
}
