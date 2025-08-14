package com.example.hl7project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDTO {
    @JsonProperty("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
