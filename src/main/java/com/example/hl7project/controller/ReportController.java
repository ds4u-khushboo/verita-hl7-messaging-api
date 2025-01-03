package com.example.hl7project.controller;

import com.example.hl7project.dto.ReportDTO;
import com.example.hl7project.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @RequestMapping("/appointmentDetails")
    public List<ReportDTO> getAppointmentDetails(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        LocalDate startDateTime = LocalDate.from(startDate.atStartOfDay());

        LocalDate endDateTime = LocalDate.from(endDate.atTime(23, 59, 59, 999999999));  // end of the day

        List<ReportDTO> appointments = reportService.getAppointmentsWithDetails(startDate, endDate);

        return appointments;
    }

//    @GetMapping("/appointments")
//    public ResponseEntity<?> getAppointmentDetails(
//            @RequestParam(required = false) Long providerId,
//            @RequestParam(required = false) Long specialtyId,
//            @RequestParam String startDate,
//            @RequestParam String endDate) {
//        return ResponseEntity.ok(reportService.get(providerId, specialtyId, startDate, endDate));
//    }
}
