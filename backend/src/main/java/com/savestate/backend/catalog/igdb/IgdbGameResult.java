package com.savestate.backend.catalog.igdb;

import java.time.LocalDate;
import java.util.List;

public record IgdbGameResult(
    long igdbId,
    String title,
    String coverUrl,
    LocalDate releaseDate,
    String trailerUrl,
    String summary,
    List<String> genres
) {
}
