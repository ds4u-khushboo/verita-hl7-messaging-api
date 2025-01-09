package com.example.hl7project.controller;

import com.example.hl7project.dto.AppointmentRequest;
import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.InboundSIUMessageRepo;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.response.MessageResponse;
import com.example.hl7project.service.*;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private InboundSIUMessageRepo inboundSIUMessageRepo;

    @Autowired
    private SIUInboundService siuInboundService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private OutboundService outboundService;

    @Autowired
    private AppointmentConfirmationService appointmentConfirmationService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NoShowService noShowService;

    @PostMapping("/SIU")
    public Message sendMessge(@RequestBody String hl7mesage) throws Exception {
        return siuInboundService.processMessage(hl7mesage);
    }

    @GetMapping("/multipleApp")
    public String getMultiple(@RequestParam String patientId) {
        appointmentConfirmationService.checkTimeDifferenceAndSendMessage(patientId, "919521052782");
        return "multiple appointment come";
    }

    @RequestMapping("/listByName")
    public List<Patient> getListByName(@RequestParam String patientName) {
        return patientRepository.findByName(patientName);
    }

    @RequestMapping("/listByPhNumber")
    public List<Patient> getListByPhNumber(@RequestParam String phNumber) {
        List<Patient> messages = patientRepository.findByHomePhone("+" + phNumber);
        return messages;
    }

    @RequestMapping("/listByTimeRange")
    public List<InboundHL7Message> getMessagesSentInRange(String startTime) {
        return inboundSIUMessageRepo.findInboundHL7MessageByCreatedAt(LocalDate.parse(startTime));
    }

    @DeleteMapping("/deleteByDate")
    public List<InboundHL7Message> getDeleteMessageByDate(@RequestParam("date") String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        return siuInboundService.deleteMessage(localDate);
    }


    @DeleteMapping("/deleteByDays")
    public List<InboundHL7Message> getDeleteMessageByDate(@RequestParam int days) {
        return siuInboundService.deleteMessagesOlderThanDays(days);
    }

    @RequestMapping("/getMessageByRange")
    public MessageResponse getMessageByTimRange(@RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return siuInboundService.getMessagesInRange(startDate, endDate);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countMessagesByType(@RequestParam String type) {
        return siuInboundService.noOfMessage(type);
    }

    @GetMapping("/noshow-rate")
    public ResponseEntity<Map<String, Object>> getNoShowRate() {
        try {
            long totalAppointments = siuInboundService.getTotalAppointmentsCount();

            Map<String, Object> response = new HashMap<>();
            response.put("totalAppointments", totalAppointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/process-no-show-reminders")
    public String processNoShowReminder() {
        String appointments = schedulerService.scheduleNoShowAppointmentsReminders();
        return "Scheduled task result: " + appointments;
    }

    @GetMapping("/providerName")
    private void getAppByProvider(@RequestParam Long patientId) {
        noShowService.handleNoShowAndRebook(patientId);
        System.out.println("Rescheduled by another provider");
    }


    @GetMapping("/count-by-type")
    public List<Object[]> getCountByMessageType() {
        return siuInboundService.getCountByMessageType();
    }


    @PostMapping("/book")
    public ResponseEntity<String> bookAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        try {
            String hl7Message = outboundService.processAppointmentRequest(appointmentRequest);
            if (hl7Message == null || hl7Message.isEmpty()) {
                throw new IllegalArgumentException("HL7 message cannot be null or empty");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing appointment: " + e.getMessage());
        }
        return ResponseEntity.status(200).body("processed");

    }
}


