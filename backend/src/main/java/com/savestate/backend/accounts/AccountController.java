package com.savestate.backend.accounts;

import com.savestate.backend.accounts.dto.LinkAccountRequest;
import com.savestate.backend.accounts.dto.LinkedAccountResponse;
import com.savestate.backend.identity.CurrentUserService;
import com.savestate.backend.identity.User;
import com.savestate.backend.shared.Provider;
import com.savestate.backend.trophies.TrophyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TrophyService trophyService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<LinkedAccountResponse> list(@AuthenticationPrincipal Jwt jwt) {
        User user = currentUserService.resolve(jwt);
        return accountService.list(user).stream().map(AccountMapper::toResponse).toList();
    }

    @PostMapping("/{provider}/link")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkedAccountResponse link(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable Provider provider,
        @Valid @RequestBody LinkAccountRequest request
    ) {
        User user = currentUserService.resolve(jwt);
        return AccountMapper.toResponse(accountService.link(user, provider, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlink(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        User user = currentUserService.resolve(jwt);
        accountService.unlink(user, id);
    }

    @PostMapping("/{id}/sync")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sync(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        User user = currentUserService.resolve(jwt);
        trophyService.syncAccount(user, id);
    }
}
