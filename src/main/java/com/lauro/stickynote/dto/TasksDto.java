package com.lauro.stickynote.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TasksDto(@JsonProperty("tasks") List<TaskDto> tasks) {

    public TasksDto(List<TaskDto> tasks){
        this.tasks = tasks;
    }

}
