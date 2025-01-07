package com.example.hl7project.service;

import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.List;
@Service
@EnableScheduling
public class SchedulerService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private NoShowService noShowService;

    private static final Logger logger = LoggerFactory.getLogger(SIUInboundService.class);

    @Scheduled(cron = "0 0 10 * * ?", zone = "America/New_York")
    public String scheduleNoShowAppointmentsReminders() {
        logger.info("Scheduled task started at: {}", LocalDateTime.now());
        return appointmentService.sendNoShowAppointmentMessages();
    }

    @Scheduled(cron = "0 0 10 * * ?", zone = "America/New_York")
    public void checkAppointmentsAndSendMessages() {
        logger.info("Scheduled task started at: {}", LocalDateTime.now(ZoneId.of("America/New_York")));

        List<Patient> allPatients = patientRepository.findAll();

        for (Patient patient : allPatients) {
            noShowService.checkAppointmentStatus(patient.getHomePhone(), patient.getPatientId());
            logger.info("Scheduled task finished at: {}", LocalDateTime.now(ZoneId.of("America/New_York")));
        }
    }
}
