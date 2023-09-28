package com.lauro.stickynote.controller;

import com.lauro.stickynote.dto.CreateTaskDto;
import com.lauro.stickynote.dto.TaskDto;
import com.lauro.stickynote.dto.TasksDto;
import com.lauro.stickynote.service.ResourceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    private ResourceService service;

    public NotesController(ResourceService service) {
        this.service = service;
    }

    @GetMapping("/home")
    public String getNotes(Model model) {
        final var notes = new TasksDto(List.of(new TaskDto(
                "Lista de compras Carrefour", List.of("pan", "pescado", "tomate")),
                new TaskDto("List de compras Aldi", List.of("Somat", "Jabon lavavajillas")),
                new TaskDto("Concesionario", List.of("Recoger llave reserva", "reivsion anual"))));

        model.addAttribute("notes", notes);
        return "home";
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
                            List.of(responseEntity.getBody().description()))));
                    modelAndView.addObject("notes", tasksDto);
                    return modelAndView;
                });
    }
}
