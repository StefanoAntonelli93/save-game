package com.savestate.backend.accounts;

import com.savestate.backend.shared.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkedAccountRepository extends JpaRepository<LinkedAccount, UUID> {

    List<LinkedAccount> findByUserId(UUID userId);

    Optional<LinkedAccount> findByIdAndUserId(UUID id, UUID userId);

    Optional<LinkedAccount> findByUserIdAndProvider(UUID userId, Provider provider);
}
