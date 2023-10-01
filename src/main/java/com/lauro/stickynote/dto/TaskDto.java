package com.lauro.stickynote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
public record TaskDto(@JsonProperty("title") String title,
                      @JsonProperty("description") String description) {

    public TaskDto(String title, String description) {
        this.title = title;
        this.description = description;
    }

}
