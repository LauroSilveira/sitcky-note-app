package com.lauro.stickynote.service;

import com.lauro.stickynote.config.Properties;
import com.lauro.stickynote.dto.CreateTaskDto;
import com.lauro.stickynote.dto.TasksDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static reactor.core.publisher.Mono.just;

@Service
@Slf4j
public class ResourceService {

    private final WebClient webClient;

    public ResourceService(WebClient.Builder builder, Properties properties) {
        this.webClient = builder.baseUrl(properties.getUrl()).build();
    }


    public Mono<ResponseEntity<CreateTaskDto>> createNote(CreateTaskDto dto, OAuth2AuthorizedClient client) {
        log.info("[ResourceService] - Request to resource server");

        return this.webClient
                .post()
                .uri("/create")
                .header("Authorization", "Bearer %s".formatted(client.getAccessToken().getTokenValue()),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(just(dto), CreateTaskDto.class)
                .exchangeToMono(this::handleResponse);
    }

    public Mono<TasksDto> getAllNotes(OAuth2AuthorizedClient client) {
        log.info("[ResourceService] - Request to resource server to retrieve all notes");

        return this.webClient.method(GET)
                .uri("/tasks/notes")
                .header("Authorization", "Bearer %s".formatted(client.getAccessToken().getTokenValue()))
                .retrieve()
                .onStatus(responseHttpStatus -> !responseHttpStatus.is2xxSuccessful(),
                        response -> Mono.error(new RuntimeException("Failed to fetch all notes. Status Code returned: " + response.statusCode())))
                .bodyToMono(TasksDto.class)
                .doOnNext(tasksDto -> new TasksDto(tasksDto.tasks()));
    }

    private Mono<ResponseEntity<CreateTaskDto>> handleResponse(final ClientResponse response) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.toEntity(CreateTaskDto.class);
        } else if (response.statusCode().is4xxClientError()) {
            return Mono.error(new Exception("Not found"));
        } else if (response.statusCode().is5xxServerError()) {
            return Mono.error(new RuntimeException("Internal Server error"));
        } else {
            return Mono.error(new RuntimeException("Unexpected error"));
        }
    }
}
