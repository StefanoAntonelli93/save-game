package com.savestate.backend.catalog.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbRawGame(
    long id,
    String name,
    Cover cover,
    Long first_release_date,
    String summary,
    List<Genre> genres,
    List<Video> videos
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cover(String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Genre(String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Video(String video_id) {
    }
}
