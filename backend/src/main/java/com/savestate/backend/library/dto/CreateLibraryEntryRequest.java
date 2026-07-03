package com.savestate.backend.library.dto;

import com.savestate.backend.library.GameStatus;
import com.savestate.backend.library.Platform;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLibraryEntryRequest(
    @NotNull UUID gameId,
    @NotNull Platform platform,
    @NotNull GameStatus status
) {
}
