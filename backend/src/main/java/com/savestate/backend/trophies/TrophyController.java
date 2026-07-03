package com.savestate.backend.trophies;

import com.savestate.backend.identity.CurrentUserService;
import com.savestate.backend.identity.User;
import com.savestate.backend.trophies.dto.TrophyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games/{gameId}/trophies")
@RequiredArgsConstructor
public class TrophyController {

    private final TrophyService trophyService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<TrophyResponse> forGame(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
        User user = currentUserService.resolve(jwt);
        return trophyService.trophiesForGame(user, gameId);
    }
}
