package com.test_task.backend.controllers;

import com.test_task.backend.models.DTOs.RepoDto;
import com.test_task.backend.services.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/org")
@CrossOrigin
public class GitHubController {

    private static final String DEFAULT_LIMIT_VALUE = "5";
    private static final String DEFAULT_SORT_KEY = "stars";
    private static final int MAX_LIMIT = 20;
    private static final int MIN_LIMIT = 1;

    private final GitHubService gitHubService;

    @GetMapping("/{org}/repos")
    public ResponseEntity<List<RepoDto>> getRepos(
            @PathVariable String org,
            @RequestParam(defaultValue = DEFAULT_LIMIT_VALUE) int limit,
            @RequestParam(defaultValue = DEFAULT_SORT_KEY) String sort
    ) {
        int safeLimit = Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, limit));
        List<RepoDto> dto = gitHubService.fetchRepos(org, safeLimit, sort);
        return ResponseEntity.ok(dto);
    }

}
