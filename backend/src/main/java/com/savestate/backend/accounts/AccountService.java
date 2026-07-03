package com.savestate.backend.accounts;

import com.savestate.backend.common.exception.ConflictException;
import com.savestate.backend.common.exception.NotFoundException;
import com.savestate.backend.identity.User;
import com.savestate.backend.accounts.dto.LinkAccountRequest;
import com.savestate.backend.shared.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final LinkedAccountRepository linkedAccountRepository;

    @Transactional(readOnly = true)
    public List<LinkedAccount> list(User user) {
        return linkedAccountRepository.findByUserId(user.getId());
    }

    @Transactional
    public LinkedAccount link(User user, Provider provider, LinkAccountRequest request) {
        linkedAccountRepository.findByUserIdAndProvider(user.getId(), provider).ifPresent(existing -> {
            throw new ConflictException("A " + provider + " account is already linked");
        });
        LinkedAccount account = LinkedAccount.builder()
            .user(user)
            .provider(provider)
            .externalId(request.externalId())
            .credentials(request.credentials())
            .build();
        return linkedAccountRepository.save(account);
    }

    @Transactional
    public void unlink(User user, UUID accountId) {
        LinkedAccount account = getOwned(user, accountId);
        linkedAccountRepository.delete(account);
    }

    @Transactional(readOnly = true)
    public LinkedAccount getOwned(User user, UUID accountId) {
        return linkedAccountRepository.findByIdAndUserId(accountId, user.getId())
            .orElseThrow(() -> new NotFoundException("Linked account not found: " + accountId));
    }
}
