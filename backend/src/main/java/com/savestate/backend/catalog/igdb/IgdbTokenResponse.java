package com.savestate.backend.catalog.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbTokenResponse(String access_token, long expires_in) {
}
