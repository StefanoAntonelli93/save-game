package com.savestate.backend.library;

import com.savestate.backend.library.dto.LibraryEntryResponse;

public final class LibraryMapper {

    private LibraryMapper() {
    }

    public static LibraryEntryResponse toResponse(LibraryEntry entry) {
        return new LibraryEntryResponse(
            entry.getId(),
            entry.getGame().getId(),
            entry.getGame().getTitle(),
            entry.getGame().getCoverUrl(),
            entry.getPlatform(),
            entry.getStatus(),
            entry.getHoursPlayed(),
            entry.getRating(),
            entry.getAddedAt(),
            entry.getUpdatedAt()
        );
    }
}
