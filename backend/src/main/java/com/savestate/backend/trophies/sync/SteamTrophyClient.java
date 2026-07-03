package com.savestate.backend.trophies.sync;

import com.savestate.backend.accounts.LinkedAccount;
import com.savestate.backend.shared.Provider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

/**
 * Steam is the one provider with an official, documented Web API
 * (ISteamUserStats) — no OAuth dance, just an API key + the user's SteamID64.
 * Requires the target Steam profile and game details to be public.
 */
@Component
public class SteamTrophyClient implements TrophyProviderClient {

    private static final String SCHEMA_URL = "https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/";
    private static final String PLAYER_ACHIEVEMENTS_URL = "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v1/";

    private final WebClient webClient;
    private final String apiKey;

    public SteamTrophyClient(WebClient.Builder webClientBuilder, @Value("${savestate.steam.api-key}") String apiKey) {
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
    }

    @Override
    public Provider provider() {
        return Provider.STEAM;
    }

    @Override
    @CircuitBreaker(name = "steam")
    @RateLimiter(name = "steam")
    public List<NormalizedTrophyDef> fetchTrophyDefinitions(String externalAppId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(SCHEMA_URL)
            .queryParam("key", apiKey)
            .queryParam("appid", externalAppId)
            .build()
            .toUri();

        SteamSchemaResponse response = webClient.get().uri(uri).retrieve()
            .bodyToMono(SteamSchemaResponse.class)
            .block();

        if (response == null || response.game() == null || response.game().availableGameStats() == null) {
            return List.of();
        }

        return response.game().availableGameStats().achievements().stream()
            .map(a -> new NormalizedTrophyDef(a.name(), a.displayName(), a.description(), null, a.icon()))
            .toList();
    }

    @Override
    @CircuitBreaker(name = "steam")
    @RateLimiter(name = "steam")
    public List<NormalizedUnlock> fetchUserUnlocks(LinkedAccount account, String externalAppId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(PLAYER_ACHIEVEMENTS_URL)
            .queryParam("key", apiKey)
            .queryParam("appid", externalAppId)
            .queryParam("steamid", account.getExternalId())
            .build()
            .toUri();

        SteamPlayerAchievementsResponse response = webClient.get().uri(uri).retrieve()
            .bodyToMono(SteamPlayerAchievementsResponse.class)
            .block();

        if (response == null || response.playerstats() == null || response.playerstats().achievements() == null) {
            return List.of();
        }

        return response.playerstats().achievements().stream()
            .filter(a -> a.achieved() == 1)
            .map(a -> new NormalizedUnlock(a.apiname(), Instant.ofEpochSecond(a.unlocktime())))
            .toList();
    }
}
