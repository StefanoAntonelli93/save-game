package com.savestate.backend.trophies.sync;

import com.savestate.backend.trophies.TrophyTier;

public record NormalizedTrophyDef(
    String externalTrophyId,
    String name,
    String description,
    TrophyTier tier,
    String iconUrl
) {
}
