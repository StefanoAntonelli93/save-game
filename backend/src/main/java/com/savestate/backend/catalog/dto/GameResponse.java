package com.savestate.backend.catalog.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GameResponse(
    UUID id,
    String title,
    String coverUrl,
    LocalDate releaseDate,
    String trailerUrl,
    String summary,
    List<String> genres
) {
}
