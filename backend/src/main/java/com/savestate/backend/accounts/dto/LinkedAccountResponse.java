package com.savestate.backend.accounts.dto;

import com.savestate.backend.accounts.AccountStatus;
import com.savestate.backend.shared.Provider;

import java.time.Instant;
import java.util.UUID;

public record LinkedAccountResponse(
    UUID id,
    Provider provider,
    String externalId,
    AccountStatus status,
    Instant lastSyncedAt
) {
}
