package com.savestate.backend.library;

import com.savestate.backend.catalog.Game;
import com.savestate.backend.catalog.GameService;
import com.savestate.backend.common.exception.ConflictException;
import com.savestate.backend.common.exception.NotFoundException;
import com.savestate.backend.identity.User;
import com.savestate.backend.library.dto.CreateLibraryEntryRequest;
import com.savestate.backend.library.dto.LibraryStatsResponse;
import com.savestate.backend.library.dto.UpdateLibraryEntryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryEntryRepository libraryEntryRepository;
    private final GameService gameService;

    @Transactional(readOnly = true)
    public List<LibraryEntry> list(User user, GameStatus status, Platform platform) {
        if (status != null && platform != null) {
            return libraryEntryRepository.findByUserIdAndStatusAndPlatform(user.getId(), status, platform);
        }
        if (status != null) {
            return libraryEntryRepository.findByUserIdAndStatus(user.getId(), status);
        }
        if (platform != null) {
            return libraryEntryRepository.findByUserIdAndPlatform(user.getId(), platform);
        }
        return libraryEntryRepository.findByUserId(user.getId());
    }

    @Transactional
    public LibraryEntry create(User user, CreateLibraryEntryRequest request) {
        if (libraryEntryRepository.existsByUserIdAndGameIdAndPlatform(user.getId(), request.gameId(), request.platform())) {
            throw new ConflictException("Game is already in the library for this platform");
        }
        Game game = gameService.getById(request.gameId());
        LibraryEntry entry = LibraryEntry.builder()
            .user(user)
            .game(game)
            .platform(request.platform())
            .status(request.status())
            .build();
        return libraryEntryRepository.save(entry);
    }

    @Transactional
    public LibraryEntry update(User user, UUID entryId, UpdateLibraryEntryRequest request) {
        LibraryEntry entry = getOwned(user, entryId);
        if (request.status() != null) {
            entry.setStatus(request.status());
        }
        if (request.hoursPlayed() != null) {
            entry.setHoursPlayed(request.hoursPlayed());
        }
        if (request.rating() != null) {
            entry.setRating(request.rating());
        }
        return libraryEntryRepository.save(entry);
    }

    @Transactional
    public void delete(User user, UUID entryId) {
        LibraryEntry entry = getOwned(user, entryId);
        libraryEntryRepository.delete(entry);
    }

    @Transactional(readOnly = true)
    public LibraryStatsResponse stats(User user) {
        List<LibraryEntry> entries = libraryEntryRepository.findByUserId(user.getId());
        long playing = entries.stream().filter(e -> e.getStatus() == GameStatus.PLAYING).count();
        long completed = entries.stream().filter(e -> e.getStatus() == GameStatus.COMPLETED).count();
        long backlog = entries.stream().filter(e -> e.getStatus() == GameStatus.BACKLOG).count();
        long wishlist = entries.stream().filter(e -> e.getStatus() == GameStatus.WISHLIST).count();
        long totalHours = entries.stream().mapToLong(LibraryEntry::getHoursPlayed).sum();
        return new LibraryStatsResponse(playing, completed, backlog, wishlist, totalHours);
    }

    private LibraryEntry getOwned(User user, UUID entryId) {
        return libraryEntryRepository.findByIdAndUserId(entryId, user.getId())
            .orElseThrow(() -> new NotFoundException("Library entry not found: " + entryId));
    }
}
