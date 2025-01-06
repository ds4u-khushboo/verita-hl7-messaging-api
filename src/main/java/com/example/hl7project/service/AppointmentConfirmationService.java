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

        if (lastAppointments != null && !lastAppointments.isEmpty()) {
            Object[] appointmentData = lastAppointments.get(0);

            long timeDiffInMinutes = (Long) appointmentData[appointmentData.length - 1];

            System.out.println("timeDiffInMinutes" + timeDiffInMinutes);
            if (timeDiffInMinutes > 180) {
                sendTextMessage(patientId, phoneNumber);
                return "Message sent successfully.";
            } else {
                return "No message sent. Time difference is less than 3 hours.";
            }
        } else {
            return "No appointment found for the patient.";
        }
    }

    private void sendTextMessage(String patientId, String phoneNumber) {
        twilioService.sendMessage(phoneNumber, textMessageConfig.getAppCreation());

        InboundHL7Message newMessage = new InboundHL7Message();

        repository.save(newMessage);
    }

}


