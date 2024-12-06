package com.example.hl7project.service;

import com.example.hl7project.dto.AppointmentTestMessageProjection;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import com.example.hl7project.model.Provider;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class NoShowService {

    @Autowired
    private TaskScheduler taskScheduler;

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
                // Check if previous appointment was "NEW" and if no message has been sent yet
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

    public void testAppointmentMessage(Long appointmentID, String patientPhone, String patientId) {
        // Fetch appointments from the repository
        List<Object[]> appointments = appointmentRepository.findOneAppointmentWithNewConfirmationStatus(patientId);

        // Iterate through the results and map them to the DTO manually
        for (Object[] rawAppointment : appointments) {
            // Manually map each Object[] to AppointmentTestMessageProjection
            AppointmentTestMessageProjection appointment = new AppointmentTestMessageProjection(
                    (Long) rawAppointment[0], // visitAppointmentId
                    (String) rawAppointment[1], // externalPatientId
                    (String) rawAppointment[2],               // cmCode
                    (Timestamp) rawAppointment[3],            // createdAt
                    ((Number) rawAppointment[4]).intValue(),  // minutesElapsed
                    ((Number) rawAppointment[5]).intValue()   // isPreviousNew
            );

            System.out.println("appointment.getCmCode()::" + appointment.getCmCode());

            // Compare visitAppointmentId to the provided appointmentID
            boolean isMatchingAppointment = appointment.getVisitAppointmentId().equals(appointmentID);
            System.out.println("appointment.getVisitAppointmentId().equals(appointmentID" + appointment.getVisitAppointmentId().equals(appointmentID));
            System.out.println("appointment()::" + isMatchingAppointment);

            if (isMatchingAppointment) {
                System.out.println("appointment.getCmCode()::" + appointment.getCmCode());

                // Compare cmCode using .equals() for value comparison
                if (appointment.getCmCode().equals("NEW")) {
                    if (appointment.getIsPreviousNew() == 0 ||
                            (appointment.getIsPreviousNew() == 1 && appointment.getMinutesElapsed() > 180)) {
                        // Send notification to the patient
                        sendMessageToPatient(patientPhone, String.format("Your appointment is scheduled for %s .", appointment.getVisitAppointmentId()));
                        // Update confirmation message code status
                        updateConfirmationMessageCodeStatus(appointment.getVisitAppointmentId(), "CRS");

                    }
                }
                break;
            }
        }
    }

//        Optional<AppointmentTestMessageProjection> appointmentTestMessageProjection = appointments.stream().filter(appt -> {
//            Long appointmetId=appt.getVisitAppointmentId();
//            System.out.println("appointmetId======"+appointmetId);
//            System.out.println("appointmetId====="+appointmentID);
//            Boolean check= appointmentID==appt.getVisitAppointmentId();
//            System.out.println("check"+check);
//            return appointmentID==appt.getVisitAppointmentId();
//                }
//        ).findFirst();
//        appointmentTestMessageProjection.ifPresent(record -> {
//            System.out.println("First Matching Record: " + record);
//
//            if (record.getCmCode() == "NEW") {
//                if (record.getIsPreviousNew() == 0 || (record.getIsPreviousNew() == 1 && record.getMinutesElapsed() > 180)) {
//                    sendMessageToPatient(patientPhone, "your appointment is scheduled");
//                    updateConfirmationMessageCodeStatus("CRS");
//                }
//            }
//        });


    public void updateConfirmationMessageCodeStatus(Long visitAppointmentId, String status) {
        Appointment optionalAppointment = appointmentRepository.findByVisitAppointmentId(visitAppointmentId);

        if (optionalAppointment != null) {
//            Appointment appointment = optionalAppointment.get();
            appointmentRepository.save(optionalAppointment); // Persist the changes
        } else {
            System.out.println("Appointment not found with ID: " + visitAppointmentId);
        }
    }
//                else if (lastAppointmentTimeMap.containsKey(patientId)) {
//                    LocalDateTime lastAppointmentTime = lastAppointmentTimeMap.get(patientId);
//                    Duration duration = Duration.between(lastAppointmentTime, createdAt.toLocalDateTime());
//                    System.out.println("patientPhone" + patientPhone);
//                    if (duration.toHours() == 3 && cmCode.equals("CRS")) {
//                        sendMessageToPatient(patientPhone, "Reminder: Your next appointment is in 3 hours.");
//                    }
//                }
//            }

//            lastAppointmentTimeMap.put(patientId, createdAt.toLocalDateTime());


    private void sendMessageToPatient(String patientPhone, String message) {
        // Placeholder for sending a message (you can integrate an actual messaging service)
        System.out.println("Sending message to patient " + patientPhone + ": " + message);
        twillioService.getTwilioService(message, "91" + patientPhone);
        System.out.println("patientPhone::::" + patientPhone);
        // messageService.sendMessage(patientId, message);
    }

//    public void checkAppointmentConfirmations() {
//        // Fetch appointments where confirmation requests have not been sent yet
//        List<Appointment> appointmentsToCheck = appointmentRepository.findByIsConfirmRequestSentTrue();
//
//        System.out.println("appointmentsToCheck: " + appointmentsToCheck);
//        for (Appointment appointment : appointmentsToCheck) {
//            // Find the most recent appointment for this patient
//            List<Appointment> patientAppointments = appointmentRepository.findByPatientAndAppointmentDate(appointment.getPatient(), appointment.getAppointmentDate());
//
//            // Sort by appointment creation time (latest first)
//            patientAppointments.sort(Comparator.comparing(Appointment::getCreatedAt).reversed());
//
//            // If there are multiple appointments, check the most recent previous appointment (last created)
//            if (patientAppointments.size() > 1) {
//                Appointment previousAppointment = patientAppointments.get(1); // The previous one (last created)
//
//                // Handle the previous appointment confirmation
//                handlePreviousAppointment(previousAppointment, appointment);  // Current appointment is passed as second appointment
//            }
//        }
//    }
//
//    private void handlePreviousAppointment(Appointment previousAppointment, Appointment currentAppointment) {
//        // Check if the confirmation request for the previous appointment has been replied to
//        if (previousAppointment.getConfirmRequestReplied() != null && previousAppointment.getConfirmRequestReplied()) {
//            // If confirmed, send the message for the second appointment immediately
//            sendSecondAppointmentMessage(currentAppointment);
//        } else {
//            scheduleMessageWithDelay(currentAppointment, 3 * 60 * 60 * 1000);
//        }
//    }
//
//    private void scheduleMessageWithDelay(Appointment secondAppointment, long delayMillis) {
//        // Log the message to track timing
//        System.out.println("Scheduling message for second appointment after 5 minutes...");
//
//        // Schedule the task with a 5-minute delay
//        taskScheduler.schedule(() -> sendSecondAppointmentMessage(secondAppointment), new Date(System.currentTimeMillis() + 300000));  // Delay for 5 minutes (300,000 ms)
//    }
//
//    private void sendSecondAppointmentMessage(Appointment secondAppointment) {
//        // Construct and send message for the second appointment
//       String message = String.format("Dear %s, we noticed you missed your first appointment. Your second appointment is scheduled for %s. Please confirm at your earliest convenience.",
//                secondAppointment.getPatient(), secondAppointment.getAppointmentDate());
//
//        // Send message via Twilio or your messaging service
//        twillioService.getTwilioService(message, "+91" + secondAppointment.getPatient().getHomePhone());
//        System.out.println("Second appointment message sent for: " + secondAppointment.getPatient().getName());
//
//        // Log the message
//        secondAppointment.setConfirmRequestSent(true);
//        appointmentRepository.save(secondAppointment);
//    }

    public Boolean handleNoShowAndRebook(Long patientId) {
        // Get patient details
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Find the latest appointment for the patient
        Appointment latestAppointment = appointmentRepository.findLatestByPatient(patientId);

        if (latestAppointment != null && "N/S".equals(latestAppointment.getVisitStatusCode())) {
            String specialty = latestAppointment.getProvider();

            // Find available providers with the same specialty
            List<Provider> availableProviders = providerRepository.findBySpecialty(specialty);

            // If there's an available provider, book the next appointment
            if (!availableProviders.isEmpty()) {
                Provider nextProvider = availableProviders.get(0);
                Appointment newAppointment = new Appointment();
                newAppointment.setPatientId(String.valueOf(patientId));
                newAppointment.setProvider(String.valueOf(nextProvider));
                newAppointment.setVisitStatusCode("PEN");
                // Save the new appointment
                appointmentRepository.save(newAppointment);

                // Send message to patient
                sendMessageToPatient(patient.getHomePhone(), String.format("Dear %s your appointment has been scheduled on %s with Dr. ", patient.getName(), newAppointment.getAppointmentDate()));
            } else {
                // No available provider, send a message about the unavailability
                sendMessageToPatient(patient.getHomePhone(), "Unfortunately, no providers are available for rescheduling your appointment.");
            }
        }
        return true;
    }

    public void checkAppointmentStatus(String patientPhone, String patientId) {
        // Check for appointments made by the patient within the last 15 and 30 days
        if (!handleNoShowAndRebook(Long.valueOf(patientId))) {
            LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

            List<Appointment> recentAppointments = appointmentRepository.findAppointmentsByPatientAndDateRange(
                     thirtyDaysAgo, LocalDateTime.now());

            boolean hasAppointmentWithin15Days = recentAppointments.stream()
                    .anyMatch(app -> LocalDateTime.parse(app.getAppointmentDateStr()).isAfter(fifteenDaysAgo));

            // If no appointment within 15 days, send message
            if (!hasAppointmentWithin15Days) {
                sendMessageToPatient(patientPhone, "You have not booked an appointment for more than 15 days. Please schedule one soon.");
            }

            // If no appointment within 30 days, send another message
            boolean hasAppointmentWithin30Days = !recentAppointments.isEmpty();
            if (!hasAppointmentWithin30Days) {
                sendMessageToPatient(patientPhone, "You have not scheduled any appointment for over 30 days. Please contact us.");
            }
        }

    }

//    public void sendMessageToPatient(String phoneNumber, String message) {
//        // Send the message via Twilio or other services
//        twillioService.getTwilioService(message, phoneNumber);
//    }

    //    public void sendMessageToPatient(Long patientId, String message) {
//        // Fetch the patient phone number
//        Patient patient = patientRepository.findById(patientId)
//                .orElseThrow(() -> new RuntimeException("Patient not found"));
//        sendMessageToPatient(patient.getHomePhone(), message);
//    }
//    private void rescheduleAppointment(Appointment appointment) {
//        Provider newProvider = findAnotherProviderWithSameSpecialty(appointment.getProviders().getSpecialty());
//        if (newProvider != null) {
//            Appointment newAppointment = new Appointment();
//            newAppointment.setPatient(appointment.getPatient());
//            newAppointment.setProviders(newProvider);
//            newAppointment.setAppointmentDate(LocalDate.now().plusDays(7).toString()); // 7 days later
//            newAppointment.setVisitStatusCode("R/S"); // Rescheduled status
//            appointmentRepository.save(newAppointment);
//
//            String message = String.format("Dear %s, your missed appointment has been rescheduled to %s. Please confirm the new appointment.",
//                    appointment.getPatient().getName(), newAppointment.getAppointmentDate());
//            twillioService.getTwilioService(message, "+91" + appointment.getPatient().getHomePhone());
//
//            System.out.println("Message Sent");
//            System.out.println("Rescheduled appointment for patient " + appointment.getPatient().getName());
//        }
//    }

    private Provider findAnotherProviderWithSameSpecialty(String specialty) {
        return providerRepository.findBySpecialty(specialty).stream().findFirst().orElse(null);
    }

}
