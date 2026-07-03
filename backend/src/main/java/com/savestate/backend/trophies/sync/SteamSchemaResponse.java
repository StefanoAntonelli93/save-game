package com.savestate.backend.trophies.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamSchemaResponse(Game game) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Game(AvailableGameStats availableGameStats) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AvailableGameStats(List<Achievement> achievements) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Achievement(String name, String displayName, String description, String icon) {
    }
}
