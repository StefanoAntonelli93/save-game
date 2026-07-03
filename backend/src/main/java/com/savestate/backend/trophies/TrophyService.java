package com.savestate.backend.trophies;

import com.savestate.backend.accounts.AccountService;
import com.savestate.backend.accounts.AccountStatus;
import com.savestate.backend.accounts.LinkedAccount;
import com.savestate.backend.accounts.LinkedAccountRepository;
import com.savestate.backend.catalog.GamePlatformId;
import com.savestate.backend.catalog.GamePlatformIdRepository;
import com.savestate.backend.identity.User;
import com.savestate.backend.library.LibraryEntry;
import com.savestate.backend.library.LibraryEntryRepository;
import com.savestate.backend.shared.Provider;
import com.savestate.backend.trophies.dto.TrophyResponse;
import com.savestate.backend.trophies.sync.NormalizedTrophyDef;
import com.savestate.backend.trophies.sync.NormalizedUnlock;
import com.savestate.backend.trophies.sync.TrophyProviderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrophyService {

    private final TrophyDefinitionRepository trophyDefinitionRepository;
    private final UserTrophyRepository userTrophyRepository;
    private final GamePlatformIdRepository gamePlatformIdRepository;
    private final LibraryEntryRepository libraryEntryRepository;
    private final LinkedAccountRepository linkedAccountRepository;
    private final AccountService accountService;
    private final Map<Provider, TrophyProviderClient> clientsByProvider;

    public TrophyService(
        TrophyDefinitionRepository trophyDefinitionRepository,
        UserTrophyRepository userTrophyRepository,
        GamePlatformIdRepository gamePlatformIdRepository,
        LibraryEntryRepository libraryEntryRepository,
        LinkedAccountRepository linkedAccountRepository,
        AccountService accountService,
        List<TrophyProviderClient> clients
    ) {
        this.trophyDefinitionRepository = trophyDefinitionRepository;
        this.userTrophyRepository = userTrophyRepository;
        this.gamePlatformIdRepository = gamePlatformIdRepository;
        this.libraryEntryRepository = libraryEntryRepository;
        this.linkedAccountRepository = linkedAccountRepository;
        this.accountService = accountService;
        this.clientsByProvider = clients.stream().collect(Collectors.toMap(TrophyProviderClient::provider, Function.identity()));
    }

    @Transactional(readOnly = true)
    public List<TrophyResponse> trophiesForGame(User user, UUID gameId) {
        List<TrophyDefinition> definitions = trophyDefinitionRepository.findByGameId(gameId);
        List<UserTrophy> unlocks = userTrophyRepository.findByUserIdAndTrophyDefinitionGameId(user.getId(), gameId);
        Map<UUID, UserTrophy> unlockByDefinitionId = unlocks.stream()
            .collect(Collectors.toMap(ut -> ut.getTrophyDefinition().getId(), Function.identity()));

        return definitions.stream()
            .map(def -> {
                UserTrophy unlock = unlockByDefinitionId.get(def.getId());
                return new TrophyResponse(
                    def.getId(), def.getProvider(), def.getName(), def.getDescription(),
                    def.getTier(), def.getIconUrl(),
                    unlock != null, unlock != null ? unlock.getUnlockedAt() : null
                );
            })
            .toList();
    }

    @Transactional
    public void syncAccount(User user, UUID linkedAccountId) {
        LinkedAccount account = accountService.getOwned(user, linkedAccountId);
        TrophyProviderClient client = clientsByProvider.get(account.getProvider());
        if (client == null) {
            throw new IllegalStateException("No client registered for provider " + account.getProvider());
        }

        List<LibraryEntry> entries = libraryEntryRepository.findByUserId(user.getId());
        try {
            for (LibraryEntry entry : entries) {
                gamePlatformIdRepository.findByGameIdAndProvider(entry.getGame().getId(), account.getProvider())
                    .ifPresent(mapping -> syncGame(user, client, account, mapping));
            }
            account.setStatus(AccountStatus.ACTIVE);
            account.setLastSyncedAt(Instant.now());
        } catch (RuntimeException ex) {
            log.warn("Trophy sync failed for account {} ({}): {}", account.getId(), account.getProvider(), ex.getMessage());
            account.setStatus(AccountStatus.ERROR);
        } finally {
            linkedAccountRepository.save(account);
        }
    }

    private void syncGame(User user, TrophyProviderClient client, LinkedAccount account, GamePlatformId mapping) {
        List<NormalizedTrophyDef> defs = client.fetchTrophyDefinitions(mapping.getExternalAppId());
        for (NormalizedTrophyDef def : defs) {
            TrophyDefinition entity = trophyDefinitionRepository
                .findByGameIdAndProviderAndExternalTrophyId(mapping.getGame().getId(), account.getProvider(), def.externalTrophyId())
                .orElseGet(() -> TrophyDefinition.builder()
                    .game(mapping.getGame())
                    .provider(account.getProvider())
                    .externalTrophyId(def.externalTrophyId())
                    .build());
            entity.setName(def.name());
            entity.setDescription(def.description());
            entity.setTier(def.tier());
            entity.setIconUrl(def.iconUrl());
            trophyDefinitionRepository.save(entity);
        }

        List<NormalizedUnlock> unlocks = client.fetchUserUnlocks(account, mapping.getExternalAppId());
        for (NormalizedUnlock unlock : unlocks) {
            trophyDefinitionRepository
                .findByGameIdAndProviderAndExternalTrophyId(mapping.getGame().getId(), account.getProvider(), unlock.externalTrophyId())
                .ifPresent(def -> {
                    UserTrophy userTrophy = userTrophyRepository.findByUserIdAndTrophyDefinitionId(user.getId(), def.getId())
                        .orElseGet(() -> UserTrophy.builder().user(user).trophyDefinition(def).build());
                    userTrophy.setUnlockedAt(unlock.unlockedAt());
                    userTrophyRepository.save(userTrophy);
                });
        }
    }
}
