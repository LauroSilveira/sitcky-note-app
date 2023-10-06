package com.lauro.stickynote.service;

import com.lauro.stickynote.dto.CreateTaskDto;
import com.lauro.stickynote.dto.TaskDto;
import com.lauro.stickynote.dto.TasksDto;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import reactor.core.publisher.Mono;

public interface ResourceService {
    Mono<TasksDto> createNote(CreateTaskDto dto, OAuth2AuthorizedClient authorizedClient);

    Mono<TasksDto> getAllNotes(OAuth2AuthorizedClient authorizedClient);

    Mono<TasksDto> updateNote(CreateTaskDto dto, OAuth2AuthorizedClient authorizedClient);

    Mono<TaskDto> getNoteById(String id, OAuth2AuthorizedClient authorizedClient);

    Mono<TasksDto> deteleNote(String id, OAuth2AuthorizedClient authorizedClient);

    Mono<Boolean> sendEmail(String id, OAuth2AuthorizedClient authorizedClient);
}
