package com.test_task.backend.services;

import com.test_task.backend.models.DTOs.RepoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;

    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.api.user-agent}")
    private String userAgent;

    @Value("${github.api.per-page}")
    private int perPage;

    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepoDto> fetchRepos(String org, int limit, String sort) {
        String url = String.format("%s/orgs/%s/repos?per_page=%d", baseUrl, org, perPage);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", userAgent);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<RepoDto[]> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RepoDto[].class
            );
        } catch (HttpClientErrorException.NotFound e) {
            return List.of();
        }

        if (response.getBody() == null) {
            return List.of();
        }

        Comparator<RepoDto> comparator = "updated".equals(sort)
                ? Comparator.comparing(RepoDto::getUpdatedAt).reversed()
                : Comparator.comparing(RepoDto::getStargazersCount).reversed();

        return Arrays.stream(response.getBody())
                .sorted(comparator)
                .limit(limit)
                .collect(Collectors.toList());
    }

}
