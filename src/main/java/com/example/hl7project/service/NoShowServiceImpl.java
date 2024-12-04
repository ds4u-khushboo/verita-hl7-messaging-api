package com.example.hl7project.service;

import com.example.hl7project.configuration.TwilioConfig;
import com.example.hl7project.model.TextMessage;
import com.example.hl7project.repository.TextMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@EnableScheduling
public class NoShowServiceImpl {

    @Autowired
    private TextMessageRepository textMessageRepository;

    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private TwillioService twillioService;

    public void sendNoShowMessage(String patientName, String patientPhone, LocalDate appointmentDate,String appointmentId) {
        String messageBody = "";
        String noshowMessage = String.format(twilioConfig.getAppNoShow(), patientName, appointmentId);
        messageBody = String.format(twilioConfig.getAppNoShow(), patientName, appointmentDate, appointmentId);
        twillioService.getTwilioService(messageBody, "91" + patientPhone);
        System.out.println("message Sent");
        TextMessage textMessage = new TextMessage();
        textMessage.setVisitAppointmentId(appointmentId);
        textMessage.setMessageBody(noshowMessage);
        textMessage.setTypeCode("NS");
        textMessageRepository.save(textMessage);
    }

    //send no show reminder 1
    public void sendNoShowReminderMessage(String patientName, String patientPhone, LocalDate appointmentDate, String appointmentId, Long textMessageId) {

        Optional<TextMessage> textMessageOptional = textMessageRepository.findById(textMessageId);

        if (textMessageOptional.isPresent()) {
            TextMessage textMessage = textMessageOptional.get();
            String typeCode = textMessage.getTypeCode();
            String messageBody = "";
            // Compare strings using equals(), not ==
            if ("NS".equals(typeCode)) {
                textMessage.setTypeCode("NSR1");
                System.out.println("patientPhone::" + patientPhone);

                messageBody = String.format(twilioConfig.getAppNoShow(), patientName, appointmentDate, appointmentId);
                twillioService.getTwilioService(messageBody, "91" + patientPhone);
                System.out.println("patientPhone::" + patientPhone);
                System.out.println("message Sent:::");
            } else if (typeCode.equals("NSR1")) {
                textMessage.setTypeCode("NSR2");
                messageBody = String.format(twilioConfig.getAppNoShow(), patientName, appointmentDate, appointmentId);
                twillioService.getTwilioService(messageBody, "91" + patientPhone);
                System.out.println("message Sent");
            }
            textMessageRepository.save(textMessage);
        } else {
            // Handle case where textMessage is not found (optional)
            System.out.println("Text message not found with ID: " + textMessageId);
        }
    }


}
