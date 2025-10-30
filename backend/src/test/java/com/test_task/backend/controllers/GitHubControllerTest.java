package com.test_task.backend.controllers;

import com.test_task.backend.models.DTOs.RepoDto;
import com.test_task.backend.services.GitHubService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GitHubController.class)
class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubService gitHubService;

    @Test
    void getRepos_defaultParams_limit5_sortStars() throws Exception {
        when(gitHubService.fetchRepos("vercel", 5, "stars"))
                .thenReturn(List.of(RepoDto.builder().name("a").build()));

        mockMvc.perform(get("/api/org/vercel/repos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(gitHubService).fetchRepos("vercel", 5, "stars");
    }

    @Test
    void getRepos_limitClampedTo20() throws Exception {
        when(gitHubService.fetchRepos(eq("netflix"), anyInt(), eq("stars")))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/org/netflix/repos?limit=100&sort=stars"))
                .andExpect(status().isOk());

        ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(gitHubService).fetchRepos(eq("netflix"), limitCaptor.capture(), eq("stars"));
        assertThat(limitCaptor.getValue()).isEqualTo(20);
    }

    @Test
    void getRepos_sortUpdated_passedThrough() throws Exception {
        when(gitHubService.fetchRepos("spring-projects", 5, "updated")).thenReturn(List.of());

        mockMvc.perform(get("/api/org/spring-projects/repos?sort=updated"))
                .andExpect(status().isOk());

        verify(gitHubService).fetchRepos("spring-projects", 5, "updated");
    }
}


