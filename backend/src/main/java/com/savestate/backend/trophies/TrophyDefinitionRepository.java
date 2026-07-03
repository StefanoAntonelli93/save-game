package com.savestate.backend.trophies;

import com.savestate.backend.shared.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrophyDefinitionRepository extends JpaRepository<TrophyDefinition, UUID> {

    List<TrophyDefinition> findByGameId(UUID gameId);

    Optional<TrophyDefinition> findByGameIdAndProviderAndExternalTrophyId(UUID gameId, Provider provider, String externalTrophyId);
}
