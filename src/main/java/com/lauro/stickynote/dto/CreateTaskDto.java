package com.lauro.stickynote.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskDto(String id, @NotBlank String title, @NotBlank String description) {
}
