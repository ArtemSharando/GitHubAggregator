package com.nortal.demo.service;

import com.nortal.demo.exception.UserNotFoundException;
import com.nortal.demo.records.Branch;
import com.nortal.demo.records.BranchDetails;
import com.nortal.demo.records.RepoDetails;
import com.nortal.demo.records.Repository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GitHubService {
    private final WebClient webClient;

    public GitHubService(WebClient.Builder webClientBuilder, @Value("${github.baseUrl}") String githubBaseUrl) {
        this.webClient = webClientBuilder.baseUrl(githubBaseUrl).build();
    }

    @CircuitBreaker(name = "github", fallbackMethod = "fallbackListRepositories")
    public Flux<RepoDetails> findAllRepositoriesByUsername(@NotBlank String username) {
        log.info("Retrieving repositories for user: {}", username);
        return webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new UserNotFoundException("User not found: " + username)))
                .bodyToFlux(Repository.class)
                .filter(repo -> !repo.fork())
                .flatMap(this::convertToRepoDetails)
                .doOnComplete(() -> log.info("Completed retrieval of repositories for user: {}", username));
    }

    private Flux<RepoDetails> fallbackListRepositories(@NotBlank String username, Throwable throwable) {
        log.error("Error occurred while retrieving repositories for user: {}. Error: {}", username, throwable.getMessage());
        return Flux.empty();
    }

    private Mono<RepoDetails> convertToRepoDetails(Repository repo) {
        return getBranchesForRepo(repo.owner().login() + "/" + repo.name())
                .collectList()
                .map(branches -> new RepoDetails(repo.name(), repo.owner().login(), branches));
    }

    private Flux<BranchDetails> getBranchesForRepo(String repoFullName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/"+repoFullName+"/branches")
                        .build())
                .retrieve()
                .bodyToFlux(Branch.class)
                .map(branch -> new BranchDetails(branch.name(), branch.commit().sha()));
    }
}
