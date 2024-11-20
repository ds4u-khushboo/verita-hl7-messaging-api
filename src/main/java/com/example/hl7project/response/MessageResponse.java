package com.example.hl7project.response;

import com.example.hl7project.model.InboundHL7Message;

import java.util.List;

public class MessageResponse {
    private List<InboundHL7Message> messages;
    private long count;

    // Constructors
    public MessageResponse(List<InboundHL7Message> messages, long count) {
        this.messages = messages;
        this.count = count;
    }

    public List<InboundHL7Message> getMessages() {
        return messages;
    }

    public void setMessages(List<InboundHL7Message> messages) {
        this.messages = messages;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
