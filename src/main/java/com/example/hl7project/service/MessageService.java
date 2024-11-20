package com.example.hl7project.service;

//import com.example.hl7project.model.InboundADTMessage;
import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.model.TextMessage;
//import com.example.hl7project.repository.InBoundADTMessageRepo;
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

//    @Autowired
//    private InBoundADTMessageRepo inBoundADTMessageRepo;

    @Autowired
    private TextMessageRepository textMessageRepository;

    public void saveMessageEntity(String messageType, String hl7Message, String phoneNumber, String appointmentId, String patientId) {
        InboundHL7Message inboundHL7Message = new InboundHL7Message();
        inboundHL7Message.setRawMessage(hl7Message);
        inboundHL7Message.setMessageType(messageType);
        inboundHL7Message.setPhoneNumber(phoneNumber);
        inboundHL7Message.setVisitAppointmentId(appointmentId);
        inboundHL7Message.setPatientId(patientId);
        inboundHL7Message.setCreatedAt(LocalDateTime.now());
        inboundSIUMessageRepo.save(inboundHL7Message);
    }

//    public void savePatientMessageEntity(String messageType, String message, String hl7Message, String phoneNumber, String patientId) {
//        InboundADTMessage inboundADTMessage = new InboundADTMessage();
//        inboundADTMessage.setRawMessage(hl7Message);
//      //  inboundADTMessage.setMessageText(message);
//        inboundADTMessage.setMessageType(messageType);
//        inboundADTMessage.setPhoneNumber(phoneNumber);
//        inboundADTMessage.setPatientId(patientId);
//        inBoundADTMessageRepo.save(inboundADTMessage);
//    }
    public void saveTextMessage(String messageType, Map<String, String> schData) {
        // Extract necessary information from the data
        String appointmentId = schData.get("Visit/Appointment ID");
        String typeCode = messageType.equals("SIU^S14") ? "NS" : "NSR1";  // Default typeCode, change based on conditions
        LocalDateTime currentTime = LocalDateTime.now();  // Record the current time for message creation
        TextMessage textMessage = new TextMessage();
        textMessage.setVisitAppointmentId(appointmentId);
        textMessage.setTypeCode(typeCode);
        textMessage.setCreatedAt(currentTime);

        // Save the text message entity to the database
        textMessageRepository.save(textMessage);
        System.out.println("Text message saved to database for appointment ID: " + appointmentId);
    }
}
