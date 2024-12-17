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
        // Call the repository method to get the most recent appointments with the time difference
        List<Object[]> lastAppointments = appointmentRepository.findAppointmentsWithTimeDiff(patientId);

        if (lastAppointments != null && !lastAppointments.isEmpty()) {
            Object[] appointmentData = lastAppointments.get(0);

            // Extract the time difference (time_diff_in_minutes) from the query result
            long timeDiffInMinutes = (Long) appointmentData[appointmentData.length - 1];

            System.out.println("timeDiffInMinutes"+timeDiffInMinutes);
            // Check if the time difference is greater than 180 minutes
            if (timeDiffInMinutes > 180) {
                sendTextMessage(patientId, phoneNumber);  // Call the method to send a text message
                return "Message sent successfully.";
            } else {
                return "No message sent. Time difference is less than 3 hours.";
            }
        } else {
            return "No appointment found for the patient.";
        }
    }

    private void sendTextMessage(String patientId,String phoneNumber) {
        twilioService.sendMessage(phoneNumber, textMessageConfig.getAppCreation());

        // Update the message status in the database
        InboundHL7Message newMessage = new InboundHL7Message();

        repository.save(newMessage);
    }

}


