package com.savestate.backend.catalog;

import com.savestate.backend.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    @Transactional(readOnly = true)
    public Page<Game> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return gameRepository.findAll(pageable);
        }
        // TODO: on a miss against the local cache, fall back to IgdbCatalogClient
        // and persist the results so future searches don't hit IGDB again.
        return gameRepository.findByTitleContainingIgnoreCase(query, pageable);
    }

    @Transactional(readOnly = true)
    public Game getById(UUID id) {
        return gameRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Game not found: " + id));
    }
}
