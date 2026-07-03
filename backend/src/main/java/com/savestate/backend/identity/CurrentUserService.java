package com.savestate.backend.identity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    @Transactional
    public User resolve(Jwt jwt) {
        String sub = jwt.getSubject();
        return userRepository.findByKeycloakSub(sub)
            .orElseGet(() -> provision(jwt));
    }

    private User provision(Jwt jwt) {
        User user = User.builder()
            .keycloakSub(jwt.getSubject())
            .email(jwt.getClaimAsString("email"))
            .displayName(displayNameFrom(jwt))
            .build();
        return userRepository.save(user);
    }

    private String displayNameFrom(Jwt jwt) {
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        if (preferredUsername != null) {
            return preferredUsername;
        }
        return jwt.getClaimAsString("name");
    }
}
