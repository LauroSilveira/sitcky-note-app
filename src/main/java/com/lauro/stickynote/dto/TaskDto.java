package com.lauro.stickynote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TaskDto(@JsonProperty("id") String id,
                      @JsonProperty("title") String title,
                      @JsonProperty("description") String description) {

    public TaskDto(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

}
