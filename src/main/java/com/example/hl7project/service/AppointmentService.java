package com.example.hl7project.service;

import com.example.hl7project.configuration.TextMessageConfig;
import com.example.hl7project.dto.AppointmentTextMessageDTO;
import com.example.hl7project.model.*;
import com.example.hl7project.repository.*;
import com.example.hl7project.utility.ConfirmationMessageStatus;
import com.example.hl7project.utility.ReminderMessageStatus;
import com.example.hl7project.utility.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private ResourceRepository resourceRepository;

    @Autowired
    private TextMessageConfig textMessageConfig;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private Utility utility;

    @Autowired
    private HL7UtilityService hl7UtilityService;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private NoShowServiceImpl noShowServiceImpl;

    public Appointment saveAppointmentData(Map<String, String> schData, Map<String, String> pv1Data, Map<String, String> aigData, Map<String, String> mshData, Map<String, String> patientData) {

        Patient patient = patientRepository.findByPatientId(patientData.get("External Patient ID"));
        if (patient == null) {
            patientService.savePatientData(patientData); // Save the patient data
        }
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentDate(utility.hl7DateToDateTime(schData.get("Appointment Date")));
        appointment.setAppointmentReason(schData.get("Appointment Reason"));
        appointment.setVisitStatusCode(schData.get("Visit Status Code"));
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
        String resourceId = aigData.get("HL7 ID");
        Provider provider = providerRepository.findByProviderId(providerCode);
        appointment.setResourceId(resourceId);
//        if (provider != null) {
//            appointment.getProvider().setProviderName(String.valueOf(provider));
//        } else {
//            System.err.println("Provider not found with code: " + providerCode);
//            // You may want to handle this case by throwing an exception or setting a default provider
//        }
        String messageDateTime = mshData.get("messageDateTime");
        if (messageDateTime != null) {
            try {
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
        Long appointmentId = Long.valueOf(schData.get("Visit/Appointment ID"));
        Appointment existingAppointment = appointmentRepository.findByVisitAppointmentId(appointmentId);

        if (existingAppointment == null) {
            System.err.println("Appointment with ID " + appointmentId + " not found.");
            return null;
        }

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

        appointmentRepository.save(existingAppointment);
        System.out.println("Appointment data updated successfully!");

        return existingAppointment;
    }

    @Transactional
    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteByVisitAppointmentId(appointmentId);
    }

    public boolean appointmentExists(Long appointmentId) {
        return appointmentRepository.existsByVisitAppointmentId(appointmentId);
    }


    public void checkAndUpdateSameSpecialtyNoShowAppointment(String patientId, String providerName) {
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
                dto.setVisitAppointmentId(Long.valueOf((String) value));
            } else {
                throw new IllegalArgumentException("Expected Long or String, but got: " + value.getClass());
            }
            dto.setPatientId((String) row[1]);
            dto.setAppointmentDate((LocalDateTime) row[2]);
            dto.setVisitStatusCode((String) row[3]);
            dto.setReminderMessageStatus((ReminderMessageStatus) row[4]);
            dto.setDays((Integer) row[5]);
            reminders.add(dto);
        }
        return reminders;
    }

    public Resource saveResourceFromAIGSegment(List<String> aigSegment) {
        Map<String, String> aigData = hl7UtilityService.extractDataFromAIGSegment(aigSegment);
        Resource resource = resourceRepository.findByResourceId(aigData.get("HL7 ID"));
        if (resource == null) {
            resource = new Resource();
            String resourceId = aigData.get("HL7 ID");
            String resourceLastName = aigData.get("Resource Last Name");
            resource.setResourceId(resourceId);
            resource.setResourceType(resourceLastName);
            resource.setStartTime("11:00");
            resource.setEndTime("17:00");
            resource.setSlotInterval(15);
        }
        return resourceRepository.save(resource);
    }

    public Location saveLocationFromAILSegment(List<String> ailSegment) {
        Map<String, String> ailData = hl7UtilityService.extractDataFromAILSegment(ailSegment);
        Location location = locationRepository.findByLocationId(ailData.get("Location HL7Id"));
        if (location == null) {
            location=new Location();
            location.setLocationId(ailData.get("Location HL7Id"));
            location.setLocationName(ailData.get("Location Name"));
        }
       return locationRepository.save(location);
    }

//    public List<Resource> calculateAvailableTimeSlots(String startTime, String resourceType) {
//        List<Resource> availableSlots = new ArrayList<>();
//        Resource resource = resourceRepository.findByStartTimeAndResourceType(LocalDateTime.parse(startTime), resourceType);
//        LocalDateTime currentTime =  resource.getStartTime();
//        while (currentTime.plusMinutes(resource.getSlotInterval()).isBefore(resource.getEndTime())) {
//            Resource resource1 = new Resource();
//            //  resource1.setResourceId(resource);
//            resource1.setStartTime(currentTime);
//            resource1.setEndTime(currentTime.plusMinutes(resource.getSlotInterval()));
//            availableSlots.add(resource1);
//            currentTime = currentTime.plusMinutes(resource.getSlotInterval());
//        }
//        return availableSlots;
//    }

    public long getAppointmentsCount(String patientId, LocalDate startDate, LocalDate endDate) {
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        LocalDateTime endDateTime = endDate.atStartOfDay();
        long count = appointmentRepository.countBookedAppointments(patientId, startDate, endDate);
        return count;
    }

    public long getNoShowAppointmentsCount(String patientId, LocalDate startDate, LocalDate endDate) {
//        LocalDate startDateTime = startDate.atStartOfDay();
//        LocalDate endDateTime = endDate.atTime(23,59,00);
        return appointmentRepository.countNoShowAppointments(patientId, startDate, endDate);
    }

    public List<Object[]> getNewAppointmentDetailsWithPatient(LocalDate startDate, LocalDate endDate, String patientId) {
        return appointmentRepository.findBookedAppointmentsWithPatientDetails(startDate, endDate, patientId);
    }

    public List<Object[]> getNoShowAppointmentDetailsWithPatient(LocalDate startDate, LocalDate endDate, String patientId) {
        return appointmentRepository.findNoShowAppointmentsWithPatientDetails(startDate, endDate, patientId);
    }

    public long getAppointmentsCount(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.countAppointmentsInDateRange(startDate, endDate);
    }

    public long getNoShowCount(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.countNoShowsInDateRange(startDate, endDate);
    }

}
