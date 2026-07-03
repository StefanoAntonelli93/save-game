package com.savestate.backend.catalog;

import com.savestate.backend.catalog.dto.GameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public Page<GameResponse> search(@RequestParam(required = false) String search, Pageable pageable) {
        return gameService.search(search, pageable).map(GameMapper::toResponse);
    }

    @GetMapping("/{id}")
    public GameResponse getById(@PathVariable UUID id) {
        return GameMapper.toResponse(gameService.getById(id));
    }
}
