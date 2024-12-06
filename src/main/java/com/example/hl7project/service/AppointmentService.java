package com.example.hl7project.service;

import com.example.hl7project.configuration.TextMessageConfig;
import com.example.hl7project.dto.AppointmentTextMessageDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import com.example.hl7project.model.Provider;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.repository.ProviderRepository;
import com.example.hl7project.utility.ConfirmationMessageStatus;
import com.example.hl7project.utility.ReminderMessageStatus;
import com.example.hl7project.utility.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TextMessageConfig textMessageConfig;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private Utility utility;
    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private NoShowServiceImpl noShowServiceImpl;

    public Appointment saveAppointmentData(Map<String, String> schData, Map<String, String> pv1Data, Map<String, String> mshData, Map<String, String> patientData) {

        Patient patient = patientRepository.findByPatientId(patientData.get("External Patient ID"));
        if (patient == null) {
            patientService.savePatientData(patientData); // Save the patient data
        }
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentDate(utility.hl7DateToDateTime(schData.get("Appointment Date")));
        appointment.setAppointmentReason(schData.get("Appointment Reason"));
        appointment.setVisitStatusCode(schData.get("Visit Status Code"));
//        appointment.setResourceName(schData.get("Resource Name"));
        appointment.setAppointmentDateStr(schData.get("Appointment Date"));
        appointment.setVisitAppointmentId(Long.valueOf(schData.get("Visit/Appointment ID")));
        appointment.setDuration(schData.get("Appointment Duration"));
        appointment.setDurationUnits(schData.get("Appointment Duration Units"));
        appointment.setAppointmentType(schData.get("Appointment Type"));
        appointment.setNotes(schData.get("Encounter Notes"));
        appointment.setPatientId(patientData.get("External Patient ID"));
        appointment.setConfirmationMessageStatus(ConfirmationMessageStatus.NONE);
        appointment.setReminderMessageStatus(ReminderMessageStatus.NONE);
        appointment.setProvider(pv1Data.get("providerId"));
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
//        appointment.setAppointmentDateUtc(LocalDate.parse(schData.get("Appointment Date")));
        String providerCode = pv1Data.get("Provider");
        Provider provider = providerRepository.findByProviderId(providerCode);
//        if (provider != null) {
//            appointment.getProvider().setProviderName(String.valueOf(provider));
//        } else {
//            System.err.println("Provider not found with code: " + providerCode);
//            // You may want to handle this case by throwing an exception or setting a default provider
//        }
        String messageDateTime = mshData.get("messageDateTime");
        if (messageDateTime != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//                LocalDateTime createdAt = LocalDateTime.parse(messageDateTime, formatter);
                appointment.setCreatedAt(LocalDateTime.now());
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing messageDateTime: " + messageDateTime);
                e.printStackTrace();
            }
        }
        appointmentRepository.save(appointment);
        System.out.println("appointment data saved!!!");
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
//Appointment appointment=existingAppointment.
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


    public void checkAndUpdateSameSpecialtyNowShowAppointment(String patientId, String providerName) {
//        Provider provider=providerRepository.findByProviderId(providerId);
        Provider provider = providerRepository.findByProviderName(providerName);
        List<Object[]> appointmentDTOList = appointmentRepository.findAppointmentsByPatientAndSpecialty(patientId, provider.getSpecialty());
        if (!appointmentDTOList.isEmpty()) {
            for (Object[] appointmentDto : appointmentDTOList) {
                Long visitAppointmentId = (Long) appointmentDto[0];
                Appointment appointment = appointmentRepository.findByVisitAppointmentId(visitAppointmentId);
                appointment.setReminderMessageStatus(ReminderMessageStatus.ABORT);
                appointmentRepository.save(appointment);
            }
        }


    }

    public String sendNoShowAppointmentMessages() {
        List<AppointmentTextMessageDTO> list = getAppointmentTextMessageDTO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        for (AppointmentTextMessageDTO appointment : list) {
            Patient patient = patientRepository.findByPatientId(appointment.getPatientId());
            ReminderMessageStatus status = appointment.getReminderMessageStatus();
            Integer days = appointment.getDays();

            if (status.equals(ReminderMessageStatus.NONE) ||
                    (status.equals(ReminderMessageStatus.NO_SHOW) && days > textMessageConfig.getNoShowReminderTwoWeekDays()) ||
                    (status.equals(ReminderMessageStatus.NO_SHOW_2_WEEK) && days > textMessageConfig.getNoShowReminderFourWeekDays())) {

                noShowServiceImpl.sendNoShowReminderMessage(
                        patient.getName(),
                        patient.getHomePhone(),
                        String.valueOf(appointment.getVisitAppointmentId())
                );
            }
        }
        return "success";
    }

    private List<AppointmentTextMessageDTO> getAppointmentTextMessageDTO() {
        List<Object[]> results = appointmentRepository.findNoShowAppointmentsToSendTextMessages();
        List<AppointmentTextMessageDTO> reminders = new ArrayList<>();
        for (Object[] row : results) {
            AppointmentTextMessageDTO dto = new AppointmentTextMessageDTO();
            Object value = row[0];
            if (value instanceof Long) {
                dto.setVisitAppointmentId((Long) value);
            } else if (value instanceof String) {
                // If it's a String, convert it to Long
                dto.setVisitAppointmentId(Long.valueOf((String) value));
            } else {
                // Handle the case where the value is neither Long nor String
                throw new IllegalArgumentException("Expected Long or String, but got: " + value.getClass());
            }
            dto.setPatientId((String) row[1]);
            dto.setAppointmentDate((LocalDateTime) row[2]);
            dto.setVisitStatusCode((String) row[3]);
            dto.setReminderMessageStatus((ReminderMessageStatus) row[4]);
            dto.setDays((Integer) row[5]);
            // Add the DTO to the list
            reminders.add(dto);
        }
        return reminders;
    }

}
