package com.savestate.backend.library;

import com.savestate.backend.identity.CurrentUserService;
import com.savestate.backend.identity.User;
import com.savestate.backend.library.dto.CreateLibraryEntryRequest;
import com.savestate.backend.library.dto.LibraryEntryResponse;
import com.savestate.backend.library.dto.LibraryStatsResponse;
import com.savestate.backend.library.dto.UpdateLibraryEntryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<LibraryEntryResponse> list(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(required = false) GameStatus status,
        @RequestParam(required = false) Platform platform
    ) {
        User user = currentUserService.resolve(jwt);
        return libraryService.list(user, status, platform).stream().map(LibraryMapper::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryEntryResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateLibraryEntryRequest request) {
        User user = currentUserService.resolve(jwt);
        return LibraryMapper.toResponse(libraryService.create(user, request));
    }

    @PatchMapping("/{id}")
    public LibraryEntryResponse update(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID id,
        @Valid @RequestBody UpdateLibraryEntryRequest request
    ) {
        User user = currentUserService.resolve(jwt);
        return LibraryMapper.toResponse(libraryService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        User user = currentUserService.resolve(jwt);
        libraryService.delete(user, id);
    }

    @GetMapping("/stats")
    public LibraryStatsResponse stats(@AuthenticationPrincipal Jwt jwt) {
        User user = currentUserService.resolve(jwt);
        return libraryService.stats(user);
    }
}
