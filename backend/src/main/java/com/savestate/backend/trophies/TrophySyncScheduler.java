package com.savestate.backend.trophies;

import com.savestate.backend.accounts.LinkedAccount;
import com.savestate.backend.accounts.LinkedAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Off by default (savestate.trophy-sync.enabled=false) so a fresh checkout
 * doesn't immediately start hammering Steam/Xbox/PSN with no credentials
 * configured. Flip it on once real API keys/tokens are in place.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "savestate.trophy-sync", name = "enabled", havingValue = "true")
public class TrophySyncScheduler {

    private final LinkedAccountRepository linkedAccountRepository;
    private final TrophyService trophyService;

    @Scheduled(cron = "${savestate.trophy-sync.interval-cron}")
    public void syncAll() {
        for (LinkedAccount account : linkedAccountRepository.findAll()) {
            try {
                trophyService.syncAccount(account.getUser(), account.getId());
            } catch (RuntimeException ex) {
                log.warn("Scheduled sync failed for linked account {}: {}", account.getId(), ex.getMessage());
            }
        }
    }
}
