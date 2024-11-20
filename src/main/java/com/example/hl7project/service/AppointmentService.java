package com.example.hl7project.service;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientService patientService;

    public Appointment saveAppointmentData(Map<String, String> schData, Map<String, String> mshData, Map<String, String> patientData) {

        Patient patient = patientRepository.findByExternalPatientId(patientData.get("External Patient ID"));
        if (patient == null) {
            patientService.savePatientData(patientData); // Save the patient data
        }
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentDate(schData.get("Appointment Date"));
        appointment.setAppointmentTime(schData.get("Appointment Time"));
        appointment.setAppointmentReason(schData.get("Appointment Reason"));
        appointment.setVisitStatusCode(schData.get("Visit Status Code"));
        appointment.setResourceName(schData.get("Resource Name"));
        appointment.setAppointmentDatetime(schData.get("Appointment Timing Quantity"));
        appointment.setVisitAppointmentId(Long.valueOf(schData.get("Visit/Appointment ID")));
        appointment.setDuration(schData.get("Appointment Duration"));
        appointment.setDurationUnits(schData.get("Appointment Duration Units"));
        appointment.setAppointmentType(schData.get("Appointment Type"));
        appointment.setNotes(schData.get("Encounter Notes"));
        appointment.setExternalPatientId(patientData.get("External Patient ID"));
        String messageDateTime = mshData.get("messageDateTime");
        if (messageDateTime != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime createdAt = LocalDateTime.parse(messageDateTime, formatter);
                appointment.setCreatedAt(createdAt);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing messageDateTime: " + messageDateTime);
                e.printStackTrace();
            }
        }
        System.out.println("appointment data saved!!!");
        appointmentRepository.save(appointment);
        return appointment;
    }

    public Appointment updateAppointmentData(Map<String, String> schData, Map<String, String> mshData) {
        // Retrieve the existing appointment using the appointment ID from schData
        Long appointmentId = Long.valueOf(schData.get("Visit/Appointment ID"));
        Appointment existingAppointment = appointmentRepository.findByVisitAppointmentId(appointmentId);

        if (existingAppointment == null) {
            // If the appointment is not found, you can handle the error or throw an exception
            System.err.println("Appointment with ID " + appointmentId + " not found.");
            return null;  // Or throw an exception
        }

        // Update the visit status code and any other relevant fields from the provided data
        if (schData.containsKey("Visit Status Code")) {
            String newVisitStatusCode = schData.get("Visit Status Code");
            existingAppointment.setVisitStatusCode(newVisitStatusCode);  // Update the visit status
        }
        String messageDateTime = mshData.get("messageDateTime");
        if (messageDateTime != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime createdAt = LocalDateTime.parse(messageDateTime, formatter);
                existingAppointment.setCreatedAt(createdAt);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing messageDateTime: " + messageDateTime);
                e.printStackTrace();
            }
        }

        // Save the updated appointment
        appointmentRepository.save(existingAppointment);
        System.out.println("Appointment data updated successfully!");

        return existingAppointment;
    }
    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteByVisitAppointmentId(appointmentId);
    }


    public boolean appointmentExists(Long appointmentId) {
        return appointmentRepository.existsByVisitAppointmentId(appointmentId);
    }
}
