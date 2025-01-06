package com.example.hl7project.controller;

import com.example.hl7project.dto.AppointmentPatientDTO;
import com.example.hl7project.dto.ReportDTO;
import com.example.hl7project.service.AppointmentService;
import com.example.hl7project.service.ReportService;
import com.example.hl7project.utility.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AppointmentService appointmentService;

    @RequestMapping("/appointmentDetails")
    public List<ReportDTO> getAppointmentDetails(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        List<ReportDTO> appointments = reportService.getAppointmentsWithDetails(startDate, endDate);

        return appointments;
    }

    @GetMapping("/createdAppointmentCount")
    public ResponseEntity<Long> getAppointmentsCount(
            @RequestParam(name = "patientId", required = false) String patientId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        long count = appointmentService.getAppointmentsCount(patientId, startDate, endDate);
        return ResponseEntity.ok(count);

    }

    @GetMapping("/noShowAppointmentCount")
    public ResponseEntity<Long> getNoShowAppointmentCount(@RequestParam(name = "patientId", required = false) String patientId,
                                                          @RequestParam(name = "startDate", required = false)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                          @RequestParam(name = "endDate", required = false)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        long count = appointmentService.getNoShowAppointmentsCount(patientId, startDate, endDate);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/findNoShowAppointmentsWithPatients")
    public List<AppointmentPatientDTO> findAppointmentsWithPatients(@RequestParam(name = "startDate", required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                    @RequestParam(name = "endDate", required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                    @RequestParam(name = "patientId", required = false) String patientId) {


        List<Object[]> appointments = appointmentService.getNoShowAppointmentDetailsWithPatient(startDate, endDate, patientId);
        return Utility.mapToDto(appointments, AppointmentPatientDTO.class);
    }


    @GetMapping("/findNewAppointmentsWithPatients")
    public List<AppointmentPatientDTO> findNewAppointmentsWithPatientsDetails(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(name = "patientId", required = false) String patientId) {

        List<Object[]> appointments = appointmentService.getNewAppointmentDetailsWithPatient(startDate, endDate, patientId);
        return Utility.mapToDto(appointments, AppointmentPatientDTO.class);
    }

    @GetMapping("/findNewAppointmentsWithProviders")
    public List<AppointmentPatientDTO> getNewAppointmentsWithProviders(
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) String specialityName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<Object[]> appointments = reportService.getBookedAppointmentsBySpecialty(providerId, specialityName, startDate, endDate);
        return Utility.mapToDto(appointments, AppointmentPatientDTO.class);
    }

    @GetMapping("/findNoShowAppointmentsWithProviders")
    public List<AppointmentPatientDTO> getNoShowAppointmentsWithProviders(
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) String specialityName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<Object[]> appointments = reportService.getNoShowAppointmentsBySpecialty(providerId, specialityName, startDate, endDate);

        List<AppointmentPatientDTO> dtos = Utility.mapToDto(appointments, AppointmentPatientDTO.class);

        dtos.forEach(dto -> System.out.println(dto));

        return dtos;
    }

    @GetMapping("/findBookedAppointmentWithLocation")
    public List<AppointmentPatientDTO> getBookedAppointmentWithLocation(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Object[]> appointments = reportService.getBookedAppointmentsByLocation(location, startDate, endDate);
        return Utility.mapToDto(appointments, AppointmentPatientDTO.class);
    }

    @GetMapping("/findNoShowAppointmentWithLocation")
    public List<AppointmentPatientDTO> getNoShowAppointmentWithLocation(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Object[]> appointments = reportService.getNoShowAppointmentsByLocation(location, startDate, endDate);
        return Utility.mapToDto(appointments, AppointmentPatientDTO.class);
    }

    @GetMapping("/findBookedAppointmentByPatientDemographics")
    public List<Map<String, Object>> getBookedAppointmentByPatientDemographics(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status) {

        List<Object[]> appointments = reportService.getBookedAppointmentsByDemographics(gender, patientName,
                minAge, maxAge, address, startDate, endDate);
        List<Map<String, Object>> mappedResults = new ArrayList<>();
        List<String> fieldNames = Arrays.asList(
                "patientId", "name", "address", "dateOfBirth", "gender", "visitStatusCode", "appointmentDate",
                "visitAppointmentId", "appointmentReason", "appointmentCount", "messagesTriggered", "age"
        );
        for (Object[] row : appointments) {
            Map<String, Object> map = new HashMap<>();

            for (int i = 0; i < row.length; i++) {
                map.put(fieldNames.get(i), row[i]);
            }

            mappedResults.add(map);
        }

        return mappedResults;
    }

    @GetMapping("/findNoShowAppointmentByPatientDemographics")
    public List<Map<String, Object>> getNoShowAppointmentByPatientDemographics(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Object[]> appointments = reportService.getNoShowAppointmentsByDemographics(gender, patientName,
                minAge, maxAge, address, startDate, endDate);
        List<Map<String, Object>> mappedResults = new ArrayList<>();
        List<String> fieldNames = Arrays.asList(
                "patientId", "name", "address", "dateOfBirth", "gender", "visitStatusCode", "appointmentDate",
                "visitAppointmentId", "appointmentReason", "appointmentCount", "messagesTriggered", "age"
        );
        for (Object[] row : appointments) {
            Map<String, Object> map = new HashMap<>();

            for (int i = 0; i < row.length; i++) {
                map.put(fieldNames.get(i), row[i]);
            }
            mappedResults.add(map);
        }
        return mappedResults;
    }
}
