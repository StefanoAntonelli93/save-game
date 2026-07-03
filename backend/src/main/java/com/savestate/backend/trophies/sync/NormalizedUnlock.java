package com.savestate.backend.trophies.sync;

import java.time.Instant;

public record NormalizedUnlock(
    String externalTrophyId,
    Instant unlockedAt
) {
}
