package com.savestate.backend.catalog;

import com.savestate.backend.shared.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GamePlatformIdRepository extends JpaRepository<GamePlatformId, UUID> {

    List<GamePlatformId> findByGameId(UUID gameId);

    Optional<GamePlatformId> findByGameIdAndProvider(UUID gameId, Provider provider);
}
