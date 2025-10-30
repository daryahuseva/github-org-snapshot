package com.test_task.backend.services;

import com.test_task.backend.models.DTOs.RepoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GitHubServiceTest {

    private RestTemplate restTemplate;
    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        gitHubService = new GitHubService(restTemplate);
        ReflectionTestUtils.setField(gitHubService, "baseUrl", "https://api.github.com");
        ReflectionTestUtils.setField(gitHubService, "userAgent", "Test-Agent");
        ReflectionTestUtils.setField(gitHubService, "perPage", 100);
    }

    @Test
    void fetchRepos_sortsByStars_desc_andLimits() {
        RepoDto r1 = RepoDto.builder().name("a").stargazersCount(5).updatedAt("2024-01-01T00:00:00Z").build();
        RepoDto r2 = RepoDto.builder().name("b").stargazersCount(10).updatedAt("2024-01-02T00:00:00Z").build();
        RepoDto r3 = RepoDto.builder().name("c").stargazersCount(7).updatedAt("2024-01-03T00:00:00Z").build();
        when(restTemplate.exchange(
                anyString(),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<String>>any(),
                ArgumentMatchers.<Class<RepoDto[]>>any()
        )).thenReturn(ResponseEntity.ok(new RepoDto[]{r1, r2, r3}));

        List<RepoDto> result = gitHubService.fetchRepos("org", 2, "stars");

        assertEquals(2, result.size());
        assertThat(result.get(0).getName()).isEqualTo("b");
        assertThat(result.get(1).getName()).isEqualTo("c");
    }

    @Test
    void fetchRepos_sortsByUpdated_desc() {
        RepoDto old = RepoDto.builder().name("old").stargazersCount(999).updatedAt("2024-01-01T00:00:00Z").build();
        RepoDto newer = RepoDto.builder().name("newer").stargazersCount(1).updatedAt("2024-02-01T00:00:00Z").build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), ArgumentMatchers.<Class<RepoDto[]>>any()))
                .thenReturn(ResponseEntity.ok(new RepoDto[]{old, newer}));

        List<RepoDto> result = gitHubService.fetchRepos("org", 5, "updated");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("newer");
        assertThat(result.get(1).getName()).isEqualTo("old");
    }

    @Test
    void fetchRepos_404_returnsEmptyList() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), ArgumentMatchers.<Class<RepoDto[]>>any()))
                .thenThrow(mock(HttpClientErrorException.NotFound.class));

        List<RepoDto> result = gitHubService.fetchRepos("nope", 5, "stars");
        assertThat(result).isEmpty();
    }

    @Test
    void fetchRepos_nullBody_returnsEmptyList() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), ArgumentMatchers.<Class<RepoDto[]>>any()))
                .thenReturn(ResponseEntity.ok(null));

        List<RepoDto> result = gitHubService.fetchRepos("org", 5, "stars");
        assertThat(result).isEmpty();
    }
}


