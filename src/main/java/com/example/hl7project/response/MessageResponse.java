package com.example.hl7project.response;

import com.example.hl7project.model.InboundHL7Message;
import lombok.Data;
import java.util.List;

@Data
public class MessageResponse {
    private List<InboundHL7Message> messages;
    private long count;

    public MessageResponse(List<InboundHL7Message> messages, long count) {
        this.messages = messages;
        this.count = count;
    }

}
