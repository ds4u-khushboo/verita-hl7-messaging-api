package com.example.hl7project.service;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.InboundSIUMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentConfirmationService {

    @Autowired
    private InboundSIUMessageRepo repository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TwillioService twilioService;

    public void processMessage(Long patientId) {
        // Step 1: Get the last appointment for the patient
        List<Appointment> lastAppointments = appointmentRepository.findLastAppointmentsByPatientId(patientId, PageRequest.of(0, 1));

        if (!lastAppointments.isEmpty()) {
            Appointment lastAppointment = lastAppointments.get(0);

            // Step 2: Check if the patient has replied to the last appointment
            if (Boolean.TRUE.equals(lastAppointment.getConfirmRequestReplied())) {
                // Send message immediately if replied
                sendMessage(lastAppointment, "+919521052782");
            } else {
                // Step 3: Check if 3 hours have passed since the last sent message
                List<InboundHL7Message> lastMessages = repository.findLastMessageByAppointmentId(String.valueOf(lastAppointment.getVisitAppointmentId()));

                if (!lastMessages.isEmpty()) {
                    InboundHL7Message lastMessage = lastMessages.get(0);
                    if (lastMessage.getSentAt() != null) {
                        LocalDateTime sentAt = LocalDateTime.parse(lastMessage.getSentAt());
                        if (sentAt.plusHours(3).isBefore(LocalDateTime.now())) {
                            // If 3 hours have passed, send the message
                            sendMessage(lastAppointment, "+919521052782");
                        } else {
                            // If 3 hours have not passed, send a notification to Twilio or log it
                            LocalDateTime nextSendTime = sentAt.plusHours(3);
                            System.out.println("Next message should be sent after: " + nextSendTime);
                            sendDelayedNotification(nextSendTime);
                        }
                    }
                }
            }
        }
    }

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

    private void sendDelayedNotification(LocalDateTime nextSendTime) {
        // Logic to send a notification indicating when the next message should be sent
        String message = "Your appointment confirmation message will be sent at: " + nextSendTime;
        String phoneNumber = "+919521052782";

        // Send a message to Twilio with the timestamp
        twilioService.sendMessage(phoneNumber, message);
    }
}


