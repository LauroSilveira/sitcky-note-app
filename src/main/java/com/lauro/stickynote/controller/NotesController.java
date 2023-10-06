package com.lauro.stickynote.controller;

import com.lauro.stickynote.dto.CreateTaskDto;
import com.lauro.stickynote.service.ResourceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import reactor.core.publisher.Mono;

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
        final var model = new ModelAndView("notes/home");
        return this.service.getAllNotes(client)
                .map(tasks -> model.addObject("tasks", tasks));
    }


    @GetMapping("/form")
    public String getForm() {
        return "notes/form";
    }

    @GetMapping("/update")
    public String getUpdate() {
        return "notes/update";
    }

    @PostMapping("/create")
    public Mono<ModelAndView> createNote(@Valid CreateTaskDto createTaskDto, BindingResult result, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        log.info("[NotesController] - received request to create new note: {}", createTaskDto);
        if (result.hasErrors()) {
            return Mono.just(new ModelAndView("notes/form"));
        }
        return this.service.createNote(createTaskDto, authorizedClient)
                .map(tasks -> {
                    final var model = new ModelAndView("notes/home");
                    return model.addObject("tasks", tasks);
                });
    }

    @GetMapping("/note/{id}")
    public Mono<ModelAndView> getNoteById(@PathVariable String id, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        log.info("[NotesController] - received request to update note: {}", id);
        final var model = new ModelAndView("notes/update");
        return this.service.getNoteById(id, authorizedClient).map(tasksDto -> {
            model.addObject("id", tasksDto.id());
            model.addObject("title", tasksDto.title());
            model.addObject("description", tasksDto.description());
            return model;
        });
    }

    @PutMapping("/update")
    public Mono<ModelAndView> updateNote(@Valid CreateTaskDto createTaskDto, BindingResult result, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        log.info("[NotesController] - received request to create new note: {}", createTaskDto);
        if (result.hasErrors()) {
            return Mono.just(new ModelAndView("notes/update"));
        }
        return this.service.updateNote(createTaskDto, authorizedClient)
                .map(tasks -> {
                    final var model = new ModelAndView("notes/home");
                    return model.addObject("tasks", tasks);
                });
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ModelAndView> deleteNote(@PathVariable String id, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        log.info("[NotesController] - received request to Delete note: {}", id);

        return this.service.deteleNote(id, authorizedClient)
                .map(tasks -> {
                    final var model = new ModelAndView("notes/home");
                    return model.addObject("tasks", tasks);
                });
    }

    @GetMapping("/email/{id}")
    public Mono<String> sendEmail(@PathVariable String id, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                                  RedirectAttributes redirectAttributes) {
        log.info("[NotesController] - Received request of User: {} to send e-mail", authorizedClient.getPrincipalName());
        return this.service.sendEmail(id, authorizedClient)
                .map(result -> {
                    if(Boolean.TRUE.equals(result)) {
                        redirectAttributes.addFlashAttribute("message", "E-mail sent with Success!");
                        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                    }else {
                        redirectAttributes.addFlashAttribute("message", "Failed to send E-mail!");
                        redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                    }
                    return "redirect:/notes/home";
                });
    }
}
