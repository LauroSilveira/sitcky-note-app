package com.lauro.stickynote.controller;

import com.lauro.stickynote.dto.CreateTaskDto;
import com.lauro.stickynote.dto.Task;
import com.lauro.stickynote.dto.Tasks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notes")
@Slf4j
public class NotesController {

    @GetMapping("/home")
    public String getNotes(Model model) {
        final var notes = new Tasks(List.of(new Task(
                "Lista de compras Carrefour", List.of("pan", "pescado", "tomate")),
                new Task("List de compras Aldi", List.of("Somat", "Jabon lavavajillas")),
                new Task("Concesionario", List.of("Recoger llave reserva", "mantemimiento"))));

        model.addAttribute("notes", notes);
        return "home";
    }

    @GetMapping("/form")
    public String getNoteForm() {
        return "notes/form";
    }

    @PostMapping("/create")
    public String createNote(CreateTaskDto dto) {
        //TODO: neviar a resource server as informacoes do DTO para que se persista
        return "notes/form";
    }
}
