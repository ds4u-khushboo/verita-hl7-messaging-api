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
        List<Object[]> lastAppointments = appointmentRepository.findAppointmentsWithTimeDiff(patientId);

        if (!lastAppointments.isEmpty() && (int) lastAppointments.get(0)[lastAppointments.get(0).length - 1] == 1) {
            sendTextMessage(patientId, phoneNumber);
            return "Message sent successfully.";
        } else {
            return "No message sent. Time difference is less than 3 hours.";
        }
    }

    private void sendTextMessage(String patientId,String phoneNumber) {
        twilioService.sendMessage(phoneNumber, textMessageConfig.getAppCreation());

        // Update the message status in the database
        InboundHL7Message newMessage = new InboundHL7Message();

        repository.save(newMessage);
    }

}


