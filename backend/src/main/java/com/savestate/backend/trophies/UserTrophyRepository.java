package com.savestate.backend.trophies;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserTrophyRepository extends JpaRepository<UserTrophy, UUID> {

    List<UserTrophy> findByUserIdAndTrophyDefinitionGameId(UUID userId, UUID gameId);

    Optional<UserTrophy> findByUserIdAndTrophyDefinitionId(UUID userId, UUID trophyDefinitionId);
}
