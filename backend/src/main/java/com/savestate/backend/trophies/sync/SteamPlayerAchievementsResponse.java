package com.savestate.backend.trophies.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SteamPlayerAchievementsResponse(Playerstats playerstats) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Playerstats(List<Achievement> achievements) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Achievement(String apiname, int achieved, Long unlocktime) {
    }
}
