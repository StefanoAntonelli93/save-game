package com.savestate.backend.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "igdb_id", unique = true)
    private Long igdbId;

    @Column(nullable = false)
    private String title;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "trailer_url")
    private String trailerUrl;

    private String summary;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(nullable = false)
    @Builder.Default
    private String[] genres = new String[0];

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
