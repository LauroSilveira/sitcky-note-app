package com.lauro.stickynote.controller;

import com.lauro.stickynote.dto.CreateTaskDto;
import com.lauro.stickynote.service.ResourceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.support.ModelAndViewContainer;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/notes")
@Slf4j
public class NotesController {

    private final ResourceService service;

    public NotesController(ResourceService service) {
        this.service = service;
    }

    @GetMapping("/form")
    public String getForm() {
        return "notes/form";
    }

    @GetMapping("/update")
    public String getUpdate() {
        return "notes/update";
    }

    @GetMapping("/home")
    public Mono<String> getAllNotes(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client, Model model) {
        log.info("[NotesController] - Request getAllNotes");
        return this.service.getAllNotes(client)
                .map(tasksDto -> model.addAttribute("tasks", tasksDto))
                .map(view -> "/notes/home");
    }


    @PostMapping("/create")
    public Mono<String> createNote(@Valid CreateTaskDto createTaskDto, BindingResult result, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient, Model model) {
        log.info("[NotesController] - received request to create new note: {}", createTaskDto);
        if (result.hasErrors()) {
            return Mono.just("notes/form");

        }
        return this.service.createNote(createTaskDto, authorizedClient)
                .map(tasks ->
                        model.addAttribute("tasks", tasks))
                .map(view -> "/notes/home");
    }

    @GetMapping("/note/{id}")
    public Mono<String> getNoteById(@PathVariable String id, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient, Model model) {
        log.info("[NotesController] - received request to update note: {}", id);
        return this.service.getNoteById(id, authorizedClient)
                .map(tasksDto -> {
            model.addAttribute("id", tasksDto.id());
            model.addAttribute("title", tasksDto.title());
            model.addAttribute("description", tasksDto.description());
            return model;
        }).map(view -> "/notes/update");
    }

    @PutMapping("/update")
    public Mono<String> updateNote(@Valid CreateTaskDto createTaskDto, BindingResult result, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient, Model model) {
        log.info("[NotesController] - received request to create new note: {}", createTaskDto);
        if (result.hasErrors()) {
            return Mono.just("notes/update");
        }
        return this.service.updateNote(createTaskDto, authorizedClient)
                .map(tasks -> model.addAttribute("tasks", tasks))
                .map(view -> "/notes/home");
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteNote(@PathVariable String id, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient, Model model) {
        log.info("[NotesController] - received request to Delete note: {}", id);
        return this.service.deteleNote(id, authorizedClient)
                .map(tasks -> model.addAttribute("tasks", tasks))
                .map(view -> "/notes/home");
    }

    @GetMapping("/email/{id}")
    public Mono<String> sendEmail(@PathVariable String id, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient, Model model) {
        log.info("[NotesController] - Received request of User: {} to send e-mail", authorizedClient.getPrincipalName());

        return this.service.sendEmail(id, authorizedClient)
                .map(result -> {
                    if (Boolean.TRUE.equals(result)) {
                        model.addAttribute("message", "E-mail sent with Success!");
                        model.addAttribute("alertClass", "alert-success");
                    } else {
                        model.addAttribute("message", "Failed to send E-mail!");
                        model.addAttribute("alertClass", "alert-danger");
                    }
                   return model;
                }).map(view ->  "notes/home");
    }
}
