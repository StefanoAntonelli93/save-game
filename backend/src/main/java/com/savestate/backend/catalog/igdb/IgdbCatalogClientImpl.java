package com.savestate.backend.catalog.igdb;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * IGDB (api-docs.igdb.com) is authenticated via a Twitch app-access token
 * (client-credentials grant), then queried with its Apicalypse query syntax.
 * Untested against live credentials — wire up IGDB_CLIENT_ID / IGDB_CLIENT_SECRET
 * and verify field mappings before relying on this in production.
 */
@Slf4j
@Component
public class IgdbCatalogClientImpl implements IgdbCatalogClient {

    private static final String TWITCH_TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String IGDB_GAMES_URL = "https://api.igdb.com/v4/games";

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final AtomicReference<CachedToken> tokenCache = new AtomicReference<>();

    public IgdbCatalogClientImpl(
        WebClient.Builder webClientBuilder,
        @Value("${savestate.igdb.client-id}") String clientId,
        @Value("${savestate.igdb.client-secret}") String clientSecret
    ) {
        this.webClient = webClientBuilder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    @CircuitBreaker(name = "igdb")
    @RateLimiter(name = "igdb")
    public List<IgdbGameResult> search(String title, int limit) {
        String token = currentAccessToken();
        String query = """
            search "%s";
            fields name,cover.url,first_release_date,summary,genres.name,videos.video_id;
            limit %d;
            """.formatted(escape(title), limit);

        IgdbRawGame[] results = webClient.post()
            .uri(IGDB_GAMES_URL)
            .header("Client-ID", clientId)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(query)
            .retrieve()
            .bodyToMono(IgdbRawGame[].class)
            .block();

        if (results == null) {
            return List.of();
        }
        return List.of(results).stream().map(this::toResult).toList();
    }

    private IgdbGameResult toResult(IgdbRawGame raw) {
        return new IgdbGameResult(
            raw.id(),
            raw.name(),
            coverUrl(raw.cover()),
            releaseDate(raw.first_release_date()),
            trailerUrl(raw.videos()),
            raw.summary(),
            raw.genres() == null ? List.of() : raw.genres().stream().map(IgdbRawGame.Genre::name).toList()
        );
    }

    private String coverUrl(IgdbRawGame.Cover cover) {
        if (cover == null || cover.url() == null) {
            return null;
        }
        String upsized = cover.url().replace("t_thumb", "t_cover_big");
        return upsized.startsWith("//") ? "https:" + upsized : upsized;
    }

    private LocalDate releaseDate(Long epochSeconds) {
        if (epochSeconds == null) {
            return null;
        }
        return Instant.ofEpochSecond(epochSeconds).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private String trailerUrl(List<IgdbRawGame.Video> videos) {
        if (videos == null || videos.isEmpty()) {
            return null;
        }
        return "https://www.youtube.com/watch?v=" + videos.get(0).video_id();
    }

    private String escape(String value) {
        return value.replace("\"", "\\\"");
    }

    private String currentAccessToken() {
        CachedToken cached = tokenCache.get();
        if (cached != null && cached.expiresAt().isAfter(Instant.now())) {
            return cached.value();
        }
        URI tokenUri = UriComponentsBuilder.fromHttpUrl(TWITCH_TOKEN_URL)
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("grant_type", "client_credentials")
            .build()
            .toUri();

        IgdbTokenResponse response = webClient.post()
            .uri(tokenUri)
            .retrieve()
            .bodyToMono(IgdbTokenResponse.class)
            .block();

        if (response == null) {
            throw new IllegalStateException("IGDB token request returned no body");
        }

        CachedToken fresh = new CachedToken(response.access_token(), Instant.now().plusSeconds(response.expires_in() - 60));
        tokenCache.set(fresh);
        return fresh.value();
    }

    private record CachedToken(String value, Instant expiresAt) {
    }
}
