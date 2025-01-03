package com.example.hl7project.service;

import com.example.hl7project.dto.ReportDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {


    @Autowired
    private AppointmentRepository appointmentRepository;


    @Autowired
    private LocationRepository locationRepository;


    public List<ReportDTO> getAppointmentsWithDetails(LocalDate startDate, LocalDate endDate) {
//            LocalDateTime startDateTime = LocalDate.from(startDate.atStartOfDay());  // Start of the day (00:00:00)
//            LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);  // End of the day (23:59:59)

        List<Appointment> appointments = appointmentRepository.findAppointmentsByDateRangeBetween(startDate.atStartOfDay(), endDate.atStartOfDay());

        // List to store transformed DTOs
        List<ReportDTO> dtoList = new ArrayList<>();

        for (Appointment appointment : appointments) {
            ReportDTO dto = new ReportDTO();
            dto.setAppointmentId(appointment.getId());
            dto.setAppointmentDate(appointment.getAppointmentDate());
            dto.setStatus(appointment.getVisitStatusCode());
//                dto.setPatientFullName(appointment.getPatient().getName());
//                dto.setPatientEmail(appointment.getPatient().getExternalPatientMRN());
//                dto.setPatientPhoneNumber(appointment.getPatient().getHomePhone());

//                // Mapping doctor details
//                dto.setDoctorFullName(appointment.getProvider().get());
//                dto.setDoctorSpecialty(appointment.getDoctor().getSpecialty());

            // Mapping messages
//                List<MessageDTO> messageDTOList = new ArrayList<>();
//                for (Message message : appointment.getM()) {
//                    MessageDTO messageDTO = new MessageDTO();
//                    messageDTO.setMessageId(message.getId());
//                    messageDTO.setMessageType(message.getMessageType());
//                    messageDTO.setContent(message.getContent());
//                    messageDTO.setSentTime(message.getSentTime());
//                    messageDTOList.add(messageDTO);
//                }
//                dto.setMessages(messageDTOList);

            // Add to the final list
            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<Object[]> getBookedAppointmentsBySpecialty(Long providerId, String specialtyName, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findBookedAppointmentsWithProviders(providerId, specialtyName, startDate, endDate);
    }

    public List<Object[]> getNoShowAppointmentsBySpecialty(Long providerId, String specialtyName, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findNoShowAppointmentsWithProviders(providerId, specialtyName, startDate, endDate);
    }

    public List<Object[]> getBookedAppointmentsByLocation(String location, LocalDate startDate, LocalDate endDate) {
        return locationRepository.findAppointmentsByLocation(location, startDate, endDate);
    }
    public List<Object[]> getNoShowAppointmentsByLocation(String location, LocalDate startDate, LocalDate endDate) {
        return locationRepository.findNoShowAppointmentsByLocation(location, startDate, endDate);
    }
    public List<Object[]> getBookedAppointmentsByDemographics(String gender, String patientName, Integer minAge, Integer maxAge, String address, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = appointmentRepository.findBookedAppointmentsWithPatientDemographics(gender, patientName, address, minAge, maxAge, startDate, endDate);

        return results;
    }

    public List<Object[]> getNoShowAppointmentsByDemographics(String gender, String patientName, Integer minAge, Integer maxAge, String address, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = appointmentRepository.findNoShowAppointmentsWithPatientDemographics(gender, patientName, address, minAge, maxAge, startDate, endDate);

        return results;
    }
}


