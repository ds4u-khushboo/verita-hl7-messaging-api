package com.example.hl7project.service;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import com.example.hl7project.model.Provider;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoShowService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TwillioService twillioService;


    public void sendConfirmationMessageForAllAppointments() {
        List<Object[]> appointmentsData = appointmentRepository.findAppointmentsWithConfirmationStatus();
        System.out.println("appointmentsData" + appointmentsData.get(0));
        Map<String, LocalDateTime> lastAppointmentTimeMap = new HashMap<>();
        Map<String, Boolean> messageSentMap = new HashMap<>();

        for (Object[] data : appointmentsData) {
            Long visitAppointmentId = (Long) data[0];
            String patientId = (String) data[1];
            String cmCode = (String) data[2];
            Timestamp createdAt = (Timestamp) data[3];
            Long minutesElapsed = (Long) data[4];
            Integer isPreviousNew = (Integer) data[5];
            Patient patient = patientRepository.findByPatientId(patientId);

            if (patient != null) {
                String patientPhone = patient.getHomePhone();
                if (cmCode.equals("NEW")) {
                    if (isPreviousNew.equals(0) || (isPreviousNew.equals(1) && minutesElapsed > 180)) {
                        sendMessageToPatient(patientPhone, "your appointment is scheduled");
                        updateConfirmationMessageCodeStatus(visitAppointmentId, "CRS");
                        messageSentMap.put(patientId, true);
                    }
                }
            }
        }
    }
    public void updateConfirmationMessageCodeStatus(Long visitAppointmentId, String status) {
        Appointment optionalAppointment = appointmentRepository.findByVisitAppointmentId(visitAppointmentId);

        if (optionalAppointment != null) {
            appointmentRepository.save(optionalAppointment);
        } else {
            System.out.println("Appointment not found with ID: " + visitAppointmentId);
        }
    }
    private void sendMessageToPatient(String patientPhone, String message) {
        System.out.println("Sending message to patient " + patientPhone + ": " + message);
        twillioService.getTwilioService(message, "91" + patientPhone);
        System.out.println("patientPhone::::" + patientPhone);
    }

    public Boolean handleNoShowAndRebook(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Appointment latestAppointment = appointmentRepository.findLatestByPatient(patientId);

        if (latestAppointment != null && "N/S".equals(latestAppointment.getVisitStatusCode())) {
            String specialty = latestAppointment.getProvider();
            List<Provider> availableProviders = providerRepository.findBySpecialty(specialty);
            if (!availableProviders.isEmpty()) {
                Provider nextProvider = availableProviders.get(0);
                Appointment newAppointment = new Appointment();
                newAppointment.setPatientId(String.valueOf(patientId));
                newAppointment.setProvider(String.valueOf(nextProvider));
                newAppointment.setVisitStatusCode("PEN");
                appointmentRepository.save(newAppointment);
                sendMessageToPatient(patient.getHomePhone(), String.format("Dear %s your appointment has been scheduled on %s with Dr. ", patient.getName(), newAppointment.getAppointmentDate()));
            } else {
                sendMessageToPatient(patient.getHomePhone(), "Unfortunately, no providers are available for rescheduling your appointment.");
            }
        }
        return true;
    }

    public void checkAppointmentStatus(String patientPhone, String patientId) {
        if (!handleNoShowAndRebook(Long.valueOf(patientId))) {
            LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

            List<Appointment> recentAppointments = appointmentRepository.findAppointmentsByPatientAndDateRange(
                    thirtyDaysAgo, LocalDateTime.now());

            boolean hasAppointmentWithin15Days = recentAppointments.stream()
                    .anyMatch(app -> LocalDateTime.parse(app.getAppointmentDateStr()).isAfter(fifteenDaysAgo));

            if (!hasAppointmentWithin15Days) {
                sendMessageToPatient(patientPhone, "You have not booked an appointment for more than 15 days. Please schedule one soon.");
            }

            boolean hasAppointmentWithin30Days = !recentAppointments.isEmpty();
            if (!hasAppointmentWithin30Days) {
                sendMessageToPatient(patientPhone, "You have not scheduled any appointment for over 30 days. Please contact us.");
            }
        }
    }
}
