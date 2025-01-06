package com.example.hl7project.service;

import com.example.hl7project.dto.ReportDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private LocationRepository locationRepository;

    public List<ReportDTO> getAppointmentsWithDetails(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching appointments with details for date range: {} to {}", startDate, endDate);

        List<Appointment> appointments = appointmentRepository.findAppointmentsByDateRangeBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
        logger.debug("Found {} appointments in the specified date range.", appointments.size());
        List<ReportDTO> dtoList = new ArrayList<>();
        for (Appointment appointment : appointments) {
            ReportDTO dto = new ReportDTO();
            dto.setAppointmentId(appointment.getId());
            dto.setAppointmentDate(appointment.getAppointmentDate());
            dto.setStatus(appointment.getVisitStatusCode());
            dtoList.add(dto);
        }

        logger.info("Returning {} appointment details.", dtoList.size());
        return dtoList;
    }

    public List<Object[]> getBookedAppointmentsBySpecialty(Long providerId, String specialtyName, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching booked appointments for providerId: {}, specialty: {}, date range: {} to {}", providerId, specialtyName, startDate, endDate);

        List<Object[]> results = appointmentRepository.findBookedAppointmentsWithProviders(providerId, specialtyName, startDate, endDate);
        logger.info("Found {} booked appointments for the specified specialty and date range.", results.size());

        return results;
    }

    public List<Object[]> getNoShowAppointmentsBySpecialty(Long providerId, String specialtyName, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching no-show appointments for providerId: {}, specialty: {}, date range: {} to {}", providerId, specialtyName, startDate, endDate);

        List<Object[]> results = appointmentRepository.findNoShowAppointmentsWithProviders(providerId, specialtyName, startDate, endDate);
        logger.info("Found {} no-show appointments for the specified specialty and date range.", results.size());

        return results;
    }

    public List<Object[]> getBookedAppointmentsByLocation(String location, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching booked appointments for location: {}, date range: {} to {}", location, startDate, endDate);

        List<Object[]> results = locationRepository.findAppointmentsByLocation(location, startDate, endDate);
        logger.info("Found {} booked appointments for the specified location and date range.", results.size());

        return results;
    }

    public List<Object[]> getNoShowAppointmentsByLocation(String location, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching no-show appointments for location: {}, date range: {} to {}", location, startDate, endDate);

        List<Object[]> results = locationRepository.findNoShowAppointmentsByLocation(location, startDate, endDate);
        logger.info("Found {} no-show appointments for the specified location and date range.", results.size());

        return results;
    }

    public List<Object[]> getBookedAppointmentsByDemographics(String gender, String patientName, Integer minAge, Integer maxAge, String address, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching booked appointments by demographics - Gender: {}, Name: {}, Address: {}, Age: {}-{}, Date Range: {} to {}",
                gender, patientName, address, minAge, maxAge, startDate, endDate);

        List<Object[]> results = appointmentRepository.findBookedAppointmentsWithPatientDemographics(gender, patientName, address, minAge, maxAge, startDate, endDate);
        logger.info("Found {} booked appointments matching the demographics.", results.size());

        return results;
    }

    public List<Object[]> getNoShowAppointmentsByDemographics(String gender, String patientName, Integer minAge, Integer maxAge, String address, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching no-show appointments by demographics - Gender: {}, Name: {}, Address: {}, Age: {}-{}, Date Range: {} to {}",
                gender, patientName, address, minAge, maxAge, startDate, endDate);

        List<Object[]> results = appointmentRepository.findNoShowAppointmentsWithPatientDemographics(gender, patientName, address, minAge, maxAge, startDate, endDate);
        logger.info("Found {} no-show appointments matching the demographics.", results.size());

        return results;
    }
}
