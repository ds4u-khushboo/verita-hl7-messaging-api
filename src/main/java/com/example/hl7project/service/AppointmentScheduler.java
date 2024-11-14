package com.example.hl7project.service;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AppointmentScheduler {

    @Autowired  // Automatically inject AppointmentRepository
    private AppointmentRepository appointmentRepository;

    private static final ZoneId EST_ZONE = ZoneId.of("America/New_York");

    public ResponseEntity<Long> getScheduler() {
        final long CHECK_INTERVAL_MINUTES = 1;

        // Create a scheduled executor to run every minute
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Scheduler triggered...");
            checkAndSendMessage();
        }, 0, CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES);

        return ResponseEntity.ok(1L); // Example response
    }

    public void checkAndSendMessage() {
        // Fetch appointments from the database
        List<Appointment> appointments = fetchAppointmentsFromDatabase();
        System.out.println("Fetched appointments: " + appointments.size());

        if (appointments.size() < 2) {
            System.out.println("Insufficient appointments found.");
            return;
        }

        // Fetch the most recent appointment (first appointment)
        Appointment firstAppointment = fetchFirstAppointment(appointments);
        Appointment secondAppointment = fetchSecondAppointment(appointments);

        // Check if appointments are valid
        if (firstAppointment == null || secondAppointment == null) {
            System.out.println("One or both appointments are null.");
            return;
        }

        // Print the first and second appointment details
        System.out.println("First Appointment ID: " + firstAppointment.getVisitAppointmentId());
        System.out.println("Second Appointment ID: " + secondAppointment.getVisitAppointmentId());

        // Case 1: If the first appointment's confirmation request was replied, send message for second appointment
        if (firstAppointment.isConfirmRequestReplied()) {
            System.out.println("First appointment's confirmation request was replied.");
            sendMessage(firstAppointment);
            firstAppointment.setConfirmed(true);
            sendMessage(secondAppointment);
        } else {
            // Case 2: If the first appointment is less than 3 hours old and not replied to, do not send message
            if (firstAppointmentCreatedWithinTimeWindow(firstAppointment)) {
                System.out.println("First appointment is less than 3 hours old and not replied. No message sent for second appointment.");
            } else {
                // Wait for confirmation of first appointment before sending second message
                System.out.println("Waiting for confirmation of the first appointment...");
                startConfirmationCheckProcess(firstAppointment);
            }
        }
    }

    private Appointment fetchFirstAppointment(List<Appointment> appointments) {
        // Fetch the first appointment (most recent)
        return appointments.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // Sort by most recent createdAt
                .findFirst()
                .orElse(null);
    }

    private Appointment fetchSecondAppointment(List<Appointment> appointments) {
        // Fetch the second most recent appointment
        return appointments.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // Sort by most recent createdAt
                .skip(1) // Skip the first one and pick the second
                .findFirst()
                .orElse(null);
    }

    public static ZonedDateTime convertHL7TimestampToEST(String hl7Timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime utcTime = LocalDateTime.parse(hl7Timestamp, formatter);
        return utcTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(EST_ZONE);
    }

    private boolean firstAppointmentCreatedWithinTimeWindow(Appointment appointment) {
        ZonedDateTime now = ZonedDateTime.now(EST_ZONE); // Get the current time in EST
        ZonedDateTime createdAt = convertHL7TimestampToEST(appointment.getCreatedAt().toString());
        Duration duration = Duration.between(createdAt, now);
        return duration.toHours() <= 3;  // 3 hours logic
    }

    private void sendMessage(Appointment appointment) {
        // Placeholder for sending a message (SMS, email, etc.)
        System.out.println("Sending confirmation message for appointment: " + appointment.getVisitAppointmentId());
    }

    private List<Appointment> fetchAppointmentsFromDatabase() {
        try {
            List<Appointment> appointments = appointmentRepository.findTop2ByOrderByCreatedAtDesc();
            if (appointments.isEmpty()) {
                System.out.println("No appointments found in the database.");
            } else {
                System.out.println("Fetched appointments: " + appointments.size());
            }
            return appointments;
        } catch (Exception e) {
            System.err.println("Error fetching appointments from database: " + e.getMessage());
            return List.of();  // Return an empty list in case of an error
        }
    }

    // New method to start polling/checking for confirmation of the first appointment
    public void startConfirmationCheckProcess(Appointment firstAppointment) {
        // Use a scheduled task to periodically check for confirmation
        ScheduledExecutorService pollingScheduler = Executors.newScheduledThreadPool(1);

        pollingScheduler.scheduleAtFixedRate(() -> {
            if (firstAppointment.isConfirmRequestReplied()) {
                // If the first appointment has received confirmation, send the second message
                System.out.println("Confirmation received for first appointment, sending second message.");

                // Fetch the second appointment
                Appointment secondAppointment = fetchSecondAppointment(fetchAppointmentsFromDatabase());
                if (secondAppointment != null) {
                    sendMessage(secondAppointment);
                }
                pollingScheduler.shutdown();  // Stop polling after confirmation
            } else {
                System.out.println("Waiting for confirmation of the first appointment...");
            }
        }, 0, 1, TimeUnit.MINUTES); // Poll every minute to check for confirmation
    }
}
