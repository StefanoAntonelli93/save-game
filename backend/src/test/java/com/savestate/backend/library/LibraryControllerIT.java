package com.savestate.backend.library;

import com.savestate.backend.catalog.Game;
import com.savestate.backend.catalog.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
class LibraryControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void createsAndListsALibraryEntryForTheAuthenticatedUser() throws Exception {
        Game game = gameRepository.save(Game.builder().title("Test Game").build());

        mockMvc.perform(post("/api/library")
                .with(jwt().jwt(j -> j.subject("user-1").claim("email", "user@example.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"gameId":"%s","platform":"PC","status":"BACKLOG"}
                    """.formatted(game.getId())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.gameTitle").value("Test Game"))
            .andExpect(jsonPath("$.status").value("BACKLOG"));

        mockMvc.perform(get("/api/library").with(jwt().jwt(j -> j.subject("user-1"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].platform").value("PC"));
    }
}
