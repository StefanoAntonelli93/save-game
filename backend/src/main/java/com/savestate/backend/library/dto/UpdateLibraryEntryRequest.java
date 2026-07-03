package com.savestate.backend.library.dto;

import com.savestate.backend.library.GameStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateLibraryEntryRequest(
    GameStatus status,
    @Min(0) Integer hoursPlayed,
    @Min(0) @Max(5) Integer rating
) {
}
