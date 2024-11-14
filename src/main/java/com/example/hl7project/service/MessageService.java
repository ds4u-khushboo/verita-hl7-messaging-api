package com.example.hl7project.service;

import com.example.hl7project.configuration.TwilioConfig;
import com.example.hl7project.model.TextMessage;
import com.example.hl7project.repository.TextMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private TwillioService twillioService;

    @Autowired
    private TextMessageRepository textMessageRepository;
    //send no show message

    @Autowired
    private TwilioConfig twilioConfig;

    public void sendNoShowMessage(String patientName, String patientPhone, String appointmentDate, String appointmentTime, String appointmentId) {
        String noshowMessage = String.format(twilioConfig.getAppNoShow(), patientName, appointmentDate, appointmentTime, appointmentId);
        twillioService.getTwilioService(noshowMessage, "+91" + patientPhone);
        TextMessage textMessage = new TextMessage();
        textMessage.setVisitAppointmentId(appointmentId);
        textMessage.setMessageBody(noshowMessage);
        textMessage.setTypeCode("NS");
        textMessageRepository.save(textMessage);
    }


    //send no show reminder 1
    public void sendNoShowReminder(String patientName, String appointmentDate, String patientPhone, String appointmentId, Long textMessageId) {

        Optional<TextMessage> textMessageOptional = textMessageRepository.findById(textMessageId);

        if (textMessageOptional.isPresent()) {
            TextMessage textMessage = textMessageOptional.get();
            String typeCode = textMessage.getTypeCode();
            String messageBody = "";
            // Compare strings using equals(), not ==
            if ("NS".equals(typeCode)) {
                textMessage.setTypeCode("NSR1");
                messageBody = String.format(twilioConfig.getAppNoShow(), patientName, appointmentDate, appointmentId);
                twillioService.getTwilioService(messageBody, "+91" + patientPhone);

            } else if (typeCode.equals("NSR1")) {
                textMessage.setTypeCode("NSR2");
            }
            textMessageRepository.save(textMessage);
        } else {
            // Handle case where textMessage is not found (optional)
            System.out.println("Text message not found with ID: " + textMessageId);
        }
    }


}
