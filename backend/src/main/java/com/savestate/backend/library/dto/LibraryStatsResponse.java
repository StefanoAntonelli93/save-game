package com.savestate.backend.library.dto;

public record LibraryStatsResponse(
    long playing,
    long completed,
    long backlog,
    long wishlist,
    long totalHoursPlayed
) {
}
