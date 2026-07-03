package com.savestate.backend.trophies.dto;

import com.savestate.backend.shared.Provider;
import com.savestate.backend.trophies.TrophyTier;

import java.time.Instant;
import java.util.UUID;

public record TrophyResponse(
    UUID id,
    Provider provider,
    String name,
    String description,
    TrophyTier tier,
    String iconUrl,
    boolean unlocked,
    Instant unlockedAt
) {
}
