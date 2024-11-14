package com.example.hl7project.response;

import com.example.hl7project.model.MessageEntity;

import java.util.List;

public class MessageResponse {
    private List<MessageEntity> messages;
    private long count;

    // Constructors
    public MessageResponse(List<MessageEntity> messages, long count) {
        this.messages = messages;
        this.count = count;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
