package com.lauro.stickynote.service;

import com.lauro.stickynote.config.Properties;
import com.lauro.stickynote.dto.CreateTaskDto;
import com.lauro.stickynote.dto.TaskDto;
import com.lauro.stickynote.dto.TasksDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.*;
import static reactor.core.publisher.Mono.just;

@Service
@Slf4j
public class ResourceService {

    private final WebClient webClient;

    public ResourceService(WebClient.Builder builder, Properties properties) {
        this.webClient = builder.baseUrl(properties.getUrl()).build();
    }


    public Mono<TasksDto> createNote(CreateTaskDto dto, OAuth2AuthorizedClient authorizedClient) {
        log.info("[ResourceService] - Request createNote to resource server");

        return this.webClient.post()
                .uri("/tasks/create")
                .header("Authorization", "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue()),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(just(dto), CreateTaskDto.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> this.handleResponse(clientResponse))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> this.handleResponse(clientResponse))
                .bodyToMono(TasksDto.class)
                .doOnNext(response -> new TasksDto(response.tasks()));
    }

    public Mono<TasksDto> getAllNotes(OAuth2AuthorizedClient authorizedClient) {
        log.info("[ResourceService] - Request getAllNotes to resource server");

        return this.webClient.method(GET)
                .uri("/tasks/notes")
                .header("Authorization", "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .onStatus(responseHttpStatus -> !responseHttpStatus.is2xxSuccessful(),
                        response -> Mono.error(new RuntimeException("Failed to fetch all notes. Status Code returned: " + response.statusCode())))
                .bodyToMono(TasksDto.class)
                .doOnNext(response -> new TasksDto(response.tasks()));
    }

    public Mono<TasksDto> updateNote(CreateTaskDto dto, OAuth2AuthorizedClient authorizedClient) {
        log.info("[ResourceService] - Request updateNote to resource server");

        return this.webClient
                .method(PUT)
                .uri("/tasks/update")
                .header("Authorization", "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue()),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(just(dto), CreateTaskDto.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> this.handleResponse(clientResponse))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> this.handleResponse(clientResponse))
                .bodyToMono(TasksDto.class)
                .doOnNext(response -> new TasksDto(response.tasks()));
    }

    public Mono<TaskDto> getNoteById(final String id, final OAuth2AuthorizedClient authorizedClient) {
        log.info("[ResourceService] - Request GetNotebyID to resource server");
        return this.webClient.method(GET)
                .uri("/tasks/note/{id}", id)
                .header("Authorization", "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue()),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(responseHttpStatus -> !responseHttpStatus.is2xxSuccessful(),
                        response -> Mono.error(new RuntimeException("Failed to fetch note. Status Code returned: " + response.statusCode())))
                .bodyToMono(TaskDto.class)
                .doOnNext(response -> new TaskDto(response.id(), response.title(), response.title()));
    }

    public Mono<TasksDto> deteleNote(String id, OAuth2AuthorizedClient authorizedClient) {
        log.info("[ResourceService] - Request Delete to resource server");
        return this.webClient
                .method(DELETE)
                .uri("/tasks/delete/{id}", id)
                .header("Authorization", "Bearer %s".formatted(authorizedClient.getAccessToken().getTokenValue()),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> this.handleResponse(clientResponse))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> this.handleResponse(clientResponse))
                .bodyToMono(TasksDto.class)
                .doOnNext(response -> new TasksDto(response.tasks()));
    }

    private Mono<? extends Throwable> handleResponse(final ClientResponse statusCode) {
        if (statusCode.statusCode().is4xxClientError()) {
            return Mono.error(new Exception("Not found"));
        } else if (statusCode.statusCode().is5xxServerError()) {
            return Mono.error(new RuntimeException("Internal Server error"));
        } else {
            return Mono.error(new RuntimeException("Unexpected error"));
        }
    }
}
