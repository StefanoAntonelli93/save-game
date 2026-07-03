package com.savestate.backend.trophies.sync;

import com.savestate.backend.accounts.LinkedAccount;
import com.savestate.backend.shared.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stub — experimental/unofficial by design. Sony has no public trophy API;
 * the only viable path is a reverse-engineered client authenticated with an
 * NPSSO token manually copied from the PSN website, which can break without
 * notice and sits outside Sony's ToS. Flagged clearly rather than hidden —
 * see the root README's provider feasibility table before enabling this.
 */
@Component
public class PsnTrophyClient implements TrophyProviderClient {

    @Override
    public Provider provider() {
        return Provider.PSN;
    }

    @Override
    public List<NormalizedTrophyDef> fetchTrophyDefinitions(String externalAppId) {
        throw new UnsupportedOperationException("PSN trophy sync not implemented yet — see backend/README.md");
    }

    @Override
    public List<NormalizedUnlock> fetchUserUnlocks(LinkedAccount account, String externalAppId) {
        throw new UnsupportedOperationException("PSN trophy sync not implemented yet — see backend/README.md");
    }
}
