package com.example.hl7project.service;

import com.example.hl7project.configuration.TextMessageConfig;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.utility.ReminderMessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class NoShowServiceImpl {

    @Autowired
    private TextMessageConfig twilioConfig;

    @Autowired
    private TwillioService twillioService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public void sendNoShowReminderMessage(String patientName, String patientPhone, String appointmentId) {

        Appointment appointment = appointmentRepository.findByVisitAppointmentId(Long.valueOf(appointmentId));
        if (appointment != null) {
            String messageBody = "";
            if (appointment.getReminderMessageStatus().equals(ReminderMessageStatus.NONE)) {
                messageBody = String.format(twilioConfig.getAppNoShow(), patientName, appointmentId);
                twillioService.getTwilioService(messageBody, patientPhone);
                appointment.setReminderMessageStatus(ReminderMessageStatus.NO_SHOW);
            } else if (appointment.getReminderMessageStatus().equals(ReminderMessageStatus.NO_SHOW)) {
                messageBody = String.format(twilioConfig.getAppointment2WeeksReminder(), patientName, appointmentId);
                twillioService.getTwilioService(messageBody, patientPhone);
                appointment.setReminderMessageStatus(ReminderMessageStatus.NO_SHOW_2_WEEK);
            } else if (appointment.getReminderMessageStatus().equals(ReminderMessageStatus.NO_SHOW_2_WEEK)) {
                messageBody = String.format(twilioConfig.getAppointment4WeeksReminder(), patientName, appointmentId);
                twillioService.getTwilioService(messageBody, patientPhone);
                appointment.setReminderMessageStatus(ReminderMessageStatus.NO_SHOW_4_WEEK);
            }
            appointmentRepository.save(appointment);
        } else {
            System.out.println("Text message not found with ID: " + appointmentId);
        }
    }
}
