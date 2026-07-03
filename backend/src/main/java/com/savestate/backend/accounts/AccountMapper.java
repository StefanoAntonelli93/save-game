package com.savestate.backend.accounts;

import com.savestate.backend.accounts.dto.LinkedAccountResponse;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static LinkedAccountResponse toResponse(LinkedAccount account) {
        return new LinkedAccountResponse(
            account.getId(),
            account.getProvider(),
            account.getExternalId(),
            account.getStatus(),
            account.getLastSyncedAt()
        );
    }
}
