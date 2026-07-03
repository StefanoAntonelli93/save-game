package com.savestate.backend.catalog;

import com.savestate.backend.catalog.dto.GameResponse;

import java.util.Arrays;

public final class GameMapper {

    private GameMapper() {
    }

    public static GameResponse toResponse(Game game) {
        return new GameResponse(
            game.getId(),
            game.getTitle(),
            game.getCoverUrl(),
            game.getReleaseDate(),
            game.getTrailerUrl(),
            game.getSummary(),
            Arrays.asList(game.getGenres())
        );
    }
}
