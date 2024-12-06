package com.example.hl7project.service;

import com.example.hl7project.configuration.TextMessageConfig;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.InboundSIUMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentConfirmationService {

    @Autowired
    private InboundSIUMessageRepo repository;

    @Autowired
    private TextMessageConfig textMessageConfig;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TwillioService twilioService;

    public String checkTimeDifferenceAndSendMessage(String patientId, String phoneNumber) {
        // Fetch the time difference result (1 or 0) for the given patientId
        List<Object[]> lastAppointments = appointmentRepository.findAppointmentsWithTimeDiff(patientId);

        // Check if the result is not empty and time difference is greater than 3 hours (1)
        if (!lastAppointments.isEmpty() && (int) lastAppointments.get(0)[lastAppointments.get(0).length - 1] == 1) {
            sendTextMessage(patientId, phoneNumber);  // Method to send the message
            return "Message sent successfully.";
        } else {
            return "No message sent. Time difference is less than 3 hours.";
        }
    }


//            // Step 2: Check if the patient has replied to the last appointment
//            if (lastAppointment.getIsConfirmRequestReplied().equals(Boolean.TRUE)) {
//                // Send message immediately if replied
//                sendMessage(lastAppointment, "+919521052782");
//            } else {
//                // Step 3: Check if 3 hours have passed since the last sent message
//                List<InboundHL7Message> lastMessages = repository.findLastMessageByAppointmentId(String.valueOf(lastAppointment.getVisitAppointmentId()));
//
//                if (!lastMessages.isEmpty()) {
//                    InboundHL7Message lastMessage = lastMessages.get(0);
//                    if (lastMessage.getSentAt() != null) {
//                        LocalDateTime sentAt = LocalDateTime.parse(lastMessage.getSentAt());
//                        if (sentAt.plusHours(3).isBefore(LocalDateTime.now())) {
//                            sendMessage(lastAppointment, "+919521052782");
//                        } else {
//                            LocalDateTime nextSendTime = sentAt.plusHours(3);
//                            System.out.println("Next message should be sent after: " + nextSendTime);
//                           // sendDelayedNotification(nextSendTime);
//                        }
//                    }
//                }
//            }
//        }
//    }

    private void sendMessage(Appointment appointment, String phoneNumber) {
        // Logic to send the confirmation message using Twilio
        System.out.println("Sending confirmation message for appointment ID: " + appointment.getVisitAppointmentId());
        // Replace with actual Twilio integration code
        twilioService.sendMessage(phoneNumber, "Your appointment is confirmed.");

        // Update the message status in the database
        InboundHL7Message newMessage = new InboundHL7Message();
        newMessage.setVisitAppointmentId(String.valueOf(appointment.getVisitAppointmentId()));
        newMessage.setSentAt(String.valueOf(LocalDateTime.now()));
        repository.save(newMessage);
    }
    private void sendTextMessage(String patientId,String phoneNumber) {
        // Logic to send the confirmation message using Twilio
//        System.out.println("Sending confirmation message for appointment ID: " + appointment.getVisitAppointmentId());
        // Replace with actual Twilio integration code
        twilioService.sendMessage(phoneNumber, textMessageConfig.getAppCreation());

        // Update the message status in the database
        InboundHL7Message newMessage = new InboundHL7Message();
//        newMessage.setVisitAppointmentId(String.valueOf(appointment.getVisitAppointmentId()));
//        newMessage.setSentAt(String.valueOf(LocalDateTime.now()));
        repository.save(newMessage);
    }

    private void sendDelayedNotification(LocalDateTime nextSendTime) {
        // Logic to send a notification indicating when the next message should be sent
        String message = "Your appointment confirmation message will be sent at: " + nextSendTime;
        String phoneNumber = "+919521052782";

        // Send a message to Twilio with the timestamp
        twilioService.sendMessage(phoneNumber, message);
    }
}


