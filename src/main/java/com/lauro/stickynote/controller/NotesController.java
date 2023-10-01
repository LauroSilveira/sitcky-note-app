package com.lauro.stickynote.controller;

import com.lauro.stickynote.dto.CreateTaskDto;
import com.lauro.stickynote.dto.TaskDto;
import com.lauro.stickynote.dto.TasksDto;
import com.lauro.stickynote.service.ResourceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/notes")
@Slf4j
public class NotesController {

    private final ResourceService service;

    public NotesController(ResourceService service) {
        this.service = service;
    }

    @GetMapping("/home")
    public Mono<ModelAndView> getAllNotes(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
        log.info("[NotesController] - Request getAllNotes");
        final var model = new ModelAndView("home");
        return this.service.getAllNotes(client)
                .map(tasksDto -> model.addObject("notes", tasksDto));
    }


    @GetMapping("/form")
    public String getForm() {
        return "notes/form";
    }

    @GetMapping("/update")
    public String getUpdateList() {
        return "notes/update";
    }

    @PostMapping("/create")
    public Mono<ModelAndView> createNote(@Valid CreateTaskDto createTaskDto, BindingResult result,
                                   @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        log.info("[NotesController] - received request to create new note: {}", createTaskDto);
        if (result.hasErrors()) {
            return Mono.just(new ModelAndView("notes/form"));
        }
        return this.service.createNote(createTaskDto, authorizedClient)
                .map(responseEntity -> {
                    var modelAndView = new ModelAndView("home");
                    final var tasksDto = new TasksDto(List.of(new TaskDto(responseEntity.getBody().title(),
                            responseEntity.getBody().description())));
                    modelAndView.addObject("notes", tasksDto);
                    return modelAndView;
                });
    }
}
