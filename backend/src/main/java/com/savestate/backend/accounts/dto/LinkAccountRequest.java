package com.savestate.backend.accounts.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Provider-specific: Steam takes a SteamID64, Xbox/PSN take the token/code
 * produced by their respective OAuth flows (handled client-side or via a
 * dedicated callback endpoint, not yet implemented here).
 */
public record LinkAccountRequest(
    @NotBlank String externalId,
    @NotBlank String credentials
) {
}
