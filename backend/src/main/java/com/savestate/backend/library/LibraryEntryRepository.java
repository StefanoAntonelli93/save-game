package com.savestate.backend.library;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, UUID> {

    List<LibraryEntry> findByUserId(UUID userId);

    List<LibraryEntry> findByUserIdAndStatus(UUID userId, GameStatus status);

    List<LibraryEntry> findByUserIdAndPlatform(UUID userId, Platform platform);

    List<LibraryEntry> findByUserIdAndStatusAndPlatform(UUID userId, GameStatus status, Platform platform);

    Optional<LibraryEntry> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndGameIdAndPlatform(UUID userId, UUID gameId, Platform platform);
}
