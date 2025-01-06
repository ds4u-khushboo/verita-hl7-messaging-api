package com.example.hl7project.service;


import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.model.TextMessage;
import com.example.hl7project.repository.InboundSIUMessageRepo;
import com.example.hl7project.repository.TextMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private InboundSIUMessageRepo inboundSIUMessageRepo;

    @Autowired
    private TextMessageRepository textMessageRepository;

    public void saveMessageEntity(String messageType, String hl7Message, String textMessage, String phoneNumber, String appointmentId, String patientId) {
        InboundHL7Message inboundHL7Message = new InboundHL7Message();
        inboundHL7Message.setRawMessage(hl7Message);
        inboundHL7Message.setMessageType(messageType);
        inboundHL7Message.setMessageText(textMessage);
        inboundHL7Message.setPhoneNumber(phoneNumber);
        inboundHL7Message.setVisitAppointmentId(appointmentId);
        inboundHL7Message.setSentAt(String.valueOf(LocalDateTime.now()));
        inboundHL7Message.setPatientId(patientId);
        inboundHL7Message.setCreatedAt(LocalDateTime.now());
        inboundSIUMessageRepo.save(inboundHL7Message);
    }

    public void saveTextMessage(String messageType, Map<String, String> schData) {
        String appointmentId = schData.get("Visit/Appointment ID");
        String typeCode = messageType.equals("SIU^S14") ? "NS" : "NSR1";
        LocalDateTime currentTime = LocalDateTime.now();
        TextMessage textMessage = new TextMessage();
        textMessage.setVisitAppointmentId(appointmentId);
        textMessage.setTypeCode(typeCode);
        textMessage.setCreatedAt(currentTime);
        textMessage.setSentAt(LocalDateTime.now());

        textMessageRepository.save(textMessage);
        System.out.println("Text message saved to database for appointment ID: " + appointmentId);
    }
}
