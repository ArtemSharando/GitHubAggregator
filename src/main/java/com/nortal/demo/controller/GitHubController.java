package com.nortal.demo.controller;

import com.nortal.demo.records.RepoDetails;
import com.nortal.demo.service.GitHubService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repositories")
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RepoDetails>>> getRepositories(@PathVariable @Pattern(
            regexp = "^[a-zA-Z0-9]+(-?[a-zA-Z0-9])*$") String username) {
        return gitHubService.findAllRepositoriesByUsername(username)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
