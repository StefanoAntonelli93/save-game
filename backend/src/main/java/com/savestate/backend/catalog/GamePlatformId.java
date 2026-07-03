package com.savestate.backend.catalog;

import com.savestate.backend.shared.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Maps a catalog game to its provider-specific app ID (e.g. Steam App ID),
 * required to fetch achievements for the right title from that provider.
 */
@Entity
@Table(name = "game_platform_ids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamePlatformId {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(name = "external_app_id", nullable = false)
    private String externalAppId;
}
