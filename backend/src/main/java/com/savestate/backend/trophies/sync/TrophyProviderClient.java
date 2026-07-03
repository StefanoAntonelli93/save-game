package com.savestate.backend.trophies.sync;

import com.savestate.backend.accounts.LinkedAccount;
import com.savestate.backend.shared.Provider;

import java.util.List;

/**
 * One implementation per trophy data source (Steam/Xbox/PSN). Each provider
 * has a wildly different auth mechanism and reliability profile — see the
 * provider feasibility table in the root README before trusting a given
 * implementation in production.
 */
public interface TrophyProviderClient {

    Provider provider();

    List<NormalizedTrophyDef> fetchTrophyDefinitions(String externalAppId);

    List<NormalizedUnlock> fetchUserUnlocks(LinkedAccount account, String externalAppId);
}
