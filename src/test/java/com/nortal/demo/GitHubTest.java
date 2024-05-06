package com.nortal.demo;

import com.nortal.demo.controller.GitHubController;
import com.nortal.demo.exception.UserNotFoundException;
import com.nortal.demo.records.BranchDetails;
import com.nortal.demo.records.RepoDetails;
import com.nortal.demo.service.GitHubService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = GitHubController.class)
class GitHubTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GitHubService gitHubService;

    @Value("${github.baseUrl}")
    private String githubBaseUrl;

    @Test
    @DisplayName("Should return list of repositories for valid username")
    void listRepositories_ValidUsername_ShouldReturnRepositories() {
        // Given
        String username = "validUser";
        RepoDetails repoDetails = new RepoDetails("repoName", "ownerLogin",
                List.of(new BranchDetails("branchName", "sha")));
        given(gitHubService.findAllRepositoriesByUsername(username)).willReturn(Flux.just(repoDetails));

        // When & Then
        webTestClient.get().uri("/repositories/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepoDetails.class).hasSize(1)
                .contains(repoDetails);
    }

    @Test
    @DisplayName("Should return 404 for non-existing GitHub user with custom message")
    void getRepositories_NonExistingUser_ShouldReturnCustomNotFoundMessage() {
        // Given
        String username = "nonExistingUser";
        given(gitHubService
                .findAllRepositoriesByUsername(username))
                .willThrow(new UserNotFoundException("User not found: " + username));

        // When & Then
        webTestClient.get().uri("/repositories/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("User not found: " + username);
    }
}
