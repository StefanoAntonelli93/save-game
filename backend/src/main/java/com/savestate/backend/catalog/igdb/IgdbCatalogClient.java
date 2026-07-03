package com.savestate.backend.catalog.igdb;

import java.util.List;

/**
 * Port to the external IGDB catalog (title, cover, release date, trailer, genres).
 * Used to backfill the local {@code games} cache on a search miss.
 */
public interface IgdbCatalogClient {

    List<IgdbGameResult> search(String title, int limit);
}
