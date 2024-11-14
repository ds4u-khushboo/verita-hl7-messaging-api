package com.example.hl7project.service;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Providers;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
@Service
public class NoShowService {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private TwillioService twillioService;

    public int getSmsSentStatus(String status) {
        List<Appointment> appointmentList = appointmentRepository.findByVisitStatusCode(status);
        if (appointmentList.isEmpty()) {
            return 0;
        }
        for (Appointment appointment : appointmentList) {
            Integer smsSentStatus = appointment.getSmsSentStatus();
            return smsSentStatus != null ? smsSentStatus : 0; // Return 0 if smsSentStatus is null
        }
        return 0;
    }
    @Scheduled(cron = "0 */15 * * * ?") // Runs every 15 minutes
    public void checkAppointmentConfirmations() {
        // Fetch appointments where confirmation requests have not been sent yet
        List<Appointment> appointmentsToCheck = appointmentRepository.findByIsConfirmRequestSentTrue();

        System.out.println("appointmentsToCheck: " + appointmentsToCheck);
        for (Appointment appointment : appointmentsToCheck) {
            // Find the most recent appointment for this patient
            List<Appointment> patientAppointments = appointmentRepository.findByPatientAndAppointmentDate(appointment.getPatient(), appointment.getAppointmentDate());

            // Sort by appointment creation time (latest first)
            patientAppointments.sort(Comparator.comparing(Appointment::getCreatedAt).reversed());

            // If there are multiple appointments, check the most recent previous appointment (last created)
            if (patientAppointments.size() > 1) {
                Appointment previousAppointment = patientAppointments.get(1); // The previous one (last created)

                // Handle the previous appointment confirmation
                handlePreviousAppointment(previousAppointment, appointment);  // Current appointment is passed as second appointment
            }
        }
    }


    private void handlePreviousAppointment(Appointment previousAppointment, Appointment currentAppointment) {
        // Check if the confirmation request for the previous appointment has been replied to
        if (previousAppointment.getConfirmRequestReplied() != null && previousAppointment.getConfirmRequestReplied()) {
            // If confirmed, send the message for the second appointment immediately
            sendSecondAppointmentMessage(currentAppointment);
        } else {
            // If not confirmed, schedule the message for the second appointment after a 5-minute delay
            scheduleMessageWithDelay(currentAppointment);
        }
    }


    // This method will introduce a delay and send the message after 5 minutes.
    private void scheduleMessageWithDelay(Appointment secondAppointment) {
        // Log the message to track timing
        System.out.println("Scheduling message for second appointment after 5 minutes...");

        // Schedule the task with a 5-minute delay
        taskScheduler.schedule(() -> sendSecondAppointmentMessage(secondAppointment), new Date(System.currentTimeMillis() + 300000));  // Delay for 5 minutes (300,000 ms)
    }

//    public List<Object[]> getAppointmentsWithoutRecentTextMessages() {
//        return appointmentRepository.findAppointmentsWithoutRecentTextMessages();
//    }
    private void sendSecondAppointmentMessage(Appointment secondAppointment) {
        // Construct and send message for the second appointment
        String message = String.format("Dear %s, we noticed you missed your first appointment. Your second appointment is scheduled for %s. Please confirm at your earliest convenience.",
                secondAppointment.getPatient(), secondAppointment.getAppointmentDate());

        // Send message via Twilio or your messaging service
        twillioService.getTwilioService(message, "+91" + secondAppointment.getPatient().getPhoneNumber());

        // Log the message
        System.out.println("Second appointment message sent for: " + secondAppointment.getPatient().getName());
    }



    // Check no-show appointments every day
    @Scheduled(cron = "0 0 9 * * ?") // Runs at 9 AM every day
    public void checkNoShowAppointments() {
        List<Appointment> noShowAppointments = appointmentRepository.findByVisitStatusCode("N/S");

        for (Appointment appointment : noShowAppointments) {
            // Handle reminders after 2 weeks
            if (shouldSendReminder(appointment, 14)) {
                sendReminder(appointment, 14);
            }

            // Handle reminders after 4 weeks
            if (shouldSendReminder(appointment, 28)) {
                sendReminder(appointment, 28);
            }

            // Reschedule appointment if not already rescheduled
            if (shouldRescheduleAppointment(appointment)) {
                rescheduleAppointment(appointment);
            }
        }
    }


    private boolean shouldSendReminder(Appointment appointment, int daysAfterAppointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // Parsing appointment datetime which includes both date and time (Appointment Timing Quantity)
        String appointmentDateTimeString = appointment.getAppointmentDate();

        if (appointmentDateTimeString == null) {
            return false; // If appointment time is not available, do not send reminder
        }

        LocalDate appointmentDateTime = LocalDate.parse(appointmentDateTimeString, formatter);

        // Calculate reminder date based on the number of days after the appointment
        LocalDate reminderDateTime = appointmentDateTime.plusDays(daysAfterAppointment);

        // Check if the reminder date has passed and SMS has been sent less than twice
        return LocalDate.now().isAfter(reminderDateTime) && appointment.getSmsSentStatus() < 2;
    }


    private void sendReminder(Appointment appointment, int weeksAfterAppointment) {
        // Retrieve the appointment date and time
        String appointmentDate = appointment.getAppointmentDate(); // e.g., "20241018" (yyyyMMdd)
        String appointmentTime = appointment.getAppointmentTime(); // e.g., "16:30:00" (HH:mm:ss)

        // Format the reminder message, including the appointment time
        String message = String.format("Dear %s, we noticed you missed your appointment on %s at %s. Please reschedule at your earliest convenience.",
                appointment.getPatient().getName(), appointmentDate, appointmentTime);

        // Send the SMS using Twilio service
        twillioService.getTwilioService(message, "+91" + appointment.getPatient().getPhoneNumber());
        System.out.println("Message Sent");

        // Update the SMS sent status
        updateSmsSentStatus(appointment, weeksAfterAppointment / 7);  // 2 for 2 weeks, 4 for 4 weeks reminder
    }

    private void updateSmsSentStatus(Appointment appointment, int newStatus) {
        appointment.setSmsSentStatus(newStatus);
        appointment.setLastMessageSentDate(LocalDate.now());
        appointmentRepository.save(appointment);
    }

    private boolean shouldRescheduleAppointment(Appointment appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate appointmentDate = LocalDate.parse(appointment.getAppointmentDate(),formatter);
        return LocalDate.now().isAfter(appointmentDate.plusDays(15)) && appointment.getVisitStatusCode().equals("N/S");
    }

    private void rescheduleAppointment(Appointment appointment) {
        Providers newProvider = findAnotherProviderWithSameSpecialty(appointment.getProviders().getSpecialty());
        if (newProvider != null) {
            Appointment newAppointment = new Appointment();
            newAppointment.setPatient(appointment.getPatient());
            newAppointment.setProviders(newProvider);
            newAppointment.setAppointmentDate(LocalDate.now().plusDays(7).toString()); // 7 days later
            newAppointment.setVisitStatusCode("R/S"); // Rescheduled status
            appointmentRepository.save(newAppointment);

            String message = String.format("Dear %s, your missed appointment has been rescheduled to %s. Please confirm the new appointment.",
                    appointment.getPatient().getName(), newAppointment.getAppointmentDate());
            twillioService.getTwilioService(message, "+91" + appointment.getPatient().getPhoneNumber());

            System.out.println("Message Sent");
            System.out.println("Rescheduled appointment for patient " + appointment.getPatient().getName());
        }
    }

    private Providers findAnotherProviderWithSameSpecialty(String specialty) {
        return providerRepository.findBySpecialty(specialty).stream().findFirst().orElse(null);
    }

}
