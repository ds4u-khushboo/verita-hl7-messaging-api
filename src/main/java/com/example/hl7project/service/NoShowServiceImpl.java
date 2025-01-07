package com.example.hl7project.service;

import com.example.hl7project.configuration.TextMessageConfig;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.utility.ReminderMessageStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
@EnableScheduling
public class NoShowServiceImpl {

    @Autowired
    private TextMessageConfig twilioConfig;

    @Autowired
    private TwillioService twillioService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private static final Logger logger = LoggerFactory.getLogger(NoShowServiceImpl.class);

    public void sendNoShowReminderMessage(String patientName, String patientPhone, String appointmentId) {
        logger.info("Sending no-show reminder for patientName: {}, patientPhone: {}, appointmentId: {}",
                patientName, patientPhone, appointmentId);
        Appointment appointment = appointmentRepository.findByVisitAppointmentId(Long.valueOf(appointmentId));
        if (appointment != null) {
            String messageBody = "";
            logger.info("Found appointment: {}, ReminderMessageStatus: {}",
                    appointmentId, appointment.getReminderMessageStatus());
            if (appointment.getReminderMessageStatus().equals(ReminderMessageStatus.NONE)) {
                messageBody = String.format(twilioConfig.getAppNoShow(), patientName, appointmentId);
                logger.info("Sending first no-show reminder: {}", messageBody);
                twillioService.getTwilioService(messageBody, patientPhone);
                appointment.setReminderMessageStatus(ReminderMessageStatus.NO_SHOW);
            } else if (appointment.getReminderMessageStatus().equals(ReminderMessageStatus.NO_SHOW)) {
                messageBody = String.format(twilioConfig.getAppointment2WeeksReminder(), patientName, appointmentId);
                logger.info("Sending two-week reminder: {}", messageBody);
                twillioService.getTwilioService(messageBody, patientPhone);
                appointment.setReminderMessageStatus(ReminderMessageStatus.NO_SHOW_2_WEEK);
            } else if (appointment.getReminderMessageStatus().equals(ReminderMessageStatus.NO_SHOW_2_WEEK)) {
                messageBody = String.format(twilioConfig.getAppointment4WeeksReminder(), patientName, appointmentId);
                logger.info("Sending four-week reminder: {}", messageBody);
                twillioService.getTwilioService(messageBody, patientPhone);
                appointment.setReminderMessageStatus(ReminderMessageStatus.NO_SHOW_4_WEEK);
            }
            appointmentRepository.save(appointment);
            logger.info("Updated appointment status to: {}", appointment.getReminderMessageStatus());
        } else {
            logger.warn("Appointment not found with ID: {}", appointmentId);
        }
    }
}
