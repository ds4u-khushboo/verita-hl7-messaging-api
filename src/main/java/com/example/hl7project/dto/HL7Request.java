package com.example.hl7project.dto;

import java.util.Map;

public class HL7Request {
    private String messageType;
    private Map<String, Object> jsonPayload;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Map<String, Object> getJsonPayload() {
        return jsonPayload;
    }

    public void setJsonPayload(Map<String, Object> jsonPayload) {
        this.jsonPayload = jsonPayload;
    }
}
