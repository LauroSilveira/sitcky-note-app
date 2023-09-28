package com.lauro.stickynote.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskDto(@NotBlank String title, @NotBlank String description) {
}
