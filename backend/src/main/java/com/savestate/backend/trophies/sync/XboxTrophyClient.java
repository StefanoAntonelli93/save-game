package com.savestate.backend.trophies.sync;

import com.savestate.backend.accounts.LinkedAccount;
import com.savestate.backend.shared.Provider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stub. Microsoft has no open first-party API for third-party achievement
 * reads without partner approval — the intended path is the unofficial
 * OpenXBL gateway (https://xbl.io), which brokers the Xbox Live OAuth flow.
 * Not wired up yet: needs an OPENXBL_API_KEY and an XBL user token exchange
 * per linked account. See the root README's provider feasibility table.
 */
@Component
public class XboxTrophyClient implements TrophyProviderClient {

    @Override
    public Provider provider() {
        return Provider.XBOX;
    }

    @Override
    public List<NormalizedTrophyDef> fetchTrophyDefinitions(String externalAppId) {
        throw new UnsupportedOperationException("Xbox trophy sync not implemented yet — see backend/README.md");
    }

    @Override
    public List<NormalizedUnlock> fetchUserUnlocks(LinkedAccount account, String externalAppId) {
        throw new UnsupportedOperationException("Xbox trophy sync not implemented yet — see backend/README.md");
    }
}
