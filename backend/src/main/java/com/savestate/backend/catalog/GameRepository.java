package com.savestate.backend.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {

    Optional<Game> findByIgdbId(Long igdbId);

    Page<Game> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
