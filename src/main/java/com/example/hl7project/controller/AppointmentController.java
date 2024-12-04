package com.example.hl7project.controller;

import com.example.hl7project.dto.AppointmentRequest;
import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.InboundSIUMessageRepo;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.response.MessageResponse;
import com.example.hl7project.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/hl7")
public class AppointmentController {

    @Autowired
    private HL7MessageBuilderService hl7MessageBuilderService;

    @Autowired
    private SIUInboundService appointmentService;

    @Autowired
    private InboundSIUMessageRepo inboundSIUMessageRepo;

    @Autowired
    private OutboundService outboundService;

    @Autowired
    private SIUInboundService siuInboundService;

    @Autowired
    private TwilioTest twilioTest;

    @Autowired
    private HL7UtilityService hl7UtilityService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SchedulerService appointmentScheduler;

    @Autowired
    private NoShowService noShowService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/SIU")
    public Message sendMessge(@RequestBody String hl7mesage) throws Exception {
        return siuInboundService.processMessage(hl7mesage);
    }

    @GetMapping("/process")
    public ResponseEntity<String> processMessages(@RequestParam Long patientId) {
        try {
            // Trigger message processing
            twilioTest.processMessage(patientId);
            return ResponseEntity.ok("Message processing completed successfully.");
        } catch (Exception e) {
            // Handle and respond with errors
            return ResponseEntity.status(500).body("Error during message processing: " + e.getMessage());
        }
    }
//    @PostMapping("/sendSMs")
//    public ResponseEntity<String> sendSMS(@RequestBody MessageDTO message) {
//        return appointmentService.getSmsConfirm(message);
//    }

//    @PostMapping("/book-appointment")
//    public void bookAppointment(@RequestBody String json) {
//        // SKIP : check duplicate patient data  by db  -patient service
//        //SKIP : if  not duplicate then create patient in db - patient service  if not created by adt inbound
//        //SKIP : create appointment in database  - if not created  by siu inbound
//        //parse json to hl7 call utility method
//        //send hl7 to mirth
//
//        HttpClient httpClient = HttpClient.newBuilder().build();
//        // httpClient.send("http://localhost:8082/hl7/convert/",json);
//    }


    @GetMapping("/multipleApp")
    public String getMultiple() {
        noShowService.checkAppointmentConfirmations();
        return "multiple appointment come";
    }

    @RequestMapping("/listByName")
    public List<Patient> getListByName(@RequestParam String patientName) {
        return patientRepository.findByName(patientName);
    }

    @PostMapping("/trigger-scheduler")
    public ResponseEntity<String> triggerScheduler() {
        schedulerService.noshowScheudler();  // Trigger the scheduled method manually
        return ResponseEntity.ok("Scheduled task triggered.");
    }

    @RequestMapping("/listByPhNumber")
    public List<Patient> getListByPhNumber(@RequestParam String phNumber) {
        List<Patient> messages = patientRepository.findByHomePhone("+" + phNumber);
        return messages;
    }

//    @PostMapping("/trigger-multiple-appointments-scheduler")
//    public ResponseEntity<String> triggerMultipleAppointmentsScheduler() {
//        schedulerService.multipleppoinmentsScheudler();  // Manually trigger the scheduled method
//        return ResponseEntity.ok("Multiple appointments scheduler triggered.");
//    }

    @RequestMapping("/listByTimeRange")
    public List<InboundHL7Message> getMessagesSentInRange(String startTime) {
        return inboundSIUMessageRepo.findInboundHL7MessageByCreatedAt(LocalDate.parse(startTime));
    }
//    @GetMapping("/trigger-no-show-check")
//    public String triggerNoShowCheck() {
//        noShowService.checkNoShowAppointments();
//
//        return "No-show appointment check triggered manually.";
//    }
//    @RequestMapping("/no-show")
//    public void getNoShow() {
//         noShowService.checkNoShowAppointments();
//    }

    @DeleteMapping("/deleteByDate")
    public List<InboundHL7Message> getDeleteMessageByDate(@RequestParam("date") String dateString) {
//        if (dateString == null) {
//            return ResponseEntity.badRequest().body("Date parameter is required and cannot be null.");
//        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse the string to LocalDateTime
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        // Convert to Timestamp
        //Timestamp timestamp = Timestamp.valueOf(String.valueOf(localDate));

        // Delete the messages
        return appointmentService.deleteMessage(localDate);
    }


    @DeleteMapping("/deleteByDays")
    public List<InboundHL7Message> getDeleteMessageByDate(@RequestParam int days) {
        return appointmentService.deleteMessagesOlderThanDays(days);
    }

    @RequestMapping("/getMessageByRange")
    public MessageResponse getMessageByTimRange(@RequestParam String startDate, @RequestParam String endDate) {
        return appointmentService.getMessagesInRange(startDate, endDate);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countMessagesByType(@RequestParam String type) {
        return appointmentService.noOfMessage(type);
    }

    //    @GetMapping("/scheduler")
//    public ResponseEntity<Long> getScheduler() {
//        return appointmentScheduler.getScheduler();
//    }
//    @GetMapping("/schedulerr")
//    public void getSchedulerr() {
//         appointmentScheduler.checkAndSendMessage();
//    }
    @GetMapping("/noshow-rate")
    public ResponseEntity<Map<String, Object>> getNoShowRate() {
        try {
            long totalAppointments = appointmentService.getTotalAppointmentsCount();
//            NoShowReportDTO noShowCount = appointmentService.getNoShowReport();
//            HashMap<String, String> appointments = appointmentService.getAppointmentDetails();
//            String messageEntities = appointmentService.getAllPatient("SIU_S26");
//            double noShowRate = (double) noShowCount / totalAppointments * 100;
            Map<String, Object> response = new HashMap<>();
            response.put("totalAppointments", totalAppointments);
            //      response.put("noShowCount", noShowCount);
//            response.put("noShowRate", noShowRate);
//            response.put("appointment", appointments);
//            response.put("Patient", messageEntities);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/without-recent-texts")

    public String getAppointmentsWithoutRecentTextMessages() {
        String appointments = siuInboundService.sendNoShowAppointmentMessages();
        System.out.println("Scheduled task result: " + appointments);

        return appointments;
    }

    //    @GetMapping("/reminder")
//    public String getReminder() {
//        String appointments = messageService.sendNoShowReminderMessage();
//        return appointments;
//    }
//    @GetMapping("/no-shows")
//    public NoShowReportDTO getNoShowReport() {
//        return appointmentService.getNoShowReport();
//    }
    @GetMapping("messgaeByStatus")
    public void getMessages() {
        try {
            schedulerService.multipleppoinmentsScheudlerWithStatus();
            System.out.println("multiple appointment come");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/providerName")
    private void getAppByProvider(@RequestParam Long patientId) {
        noShowService.handleNoShowAndRebook(patientId);
        System.out.println("Rescheduled by another provider");
    }

    @GetMapping("/appointmetnCheck")
    private void checkStatau() {
        schedulerService.checkAppointmentsAndSendMessages();
        System.out.println("checked appointment status");
    }

    @GetMapping("/count-by-type")
    public List<Object[]> getCountByMessageType() {
        return appointmentService.getCountByMessageType();
    }

    @PostMapping("/book-appointment")
    public String conversion(@RequestBody AppointmentRequest appointmentRequest) {
        return outboundService.processAppointmentRequest(appointmentRequest);
    }

    @PostMapping("/siubuild")
    public String buildSIU(@RequestBody AppointmentRequest appointmentRequest) {
        String hl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
        System.out.println("hl7Message::" + hl7Message);
        return hl7Message.toString();
    }

    private String sendHl7ToMirth(String hl7Message) throws Exception {
        String mirthEndpoint = "https://10.0.1.52:8443/api/channels/21ceec35-d53a-42cf-ab70-059353d21454?destinationMetaDataId=1";

        HttpClient httpClient = HttpClient.newBuilder().build();

        // Replace with your Mirth username and password
        String username = "admin";
        String password = "admin";
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        // Prepare HTTP POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(mirthEndpoint))
                .header("Content-Type", "text/plain")
                .header("Authorization", authHeader)
                .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
                .build();

        // Send the request and receive the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Mirth Response: " + response.body());
            return "Successfully sent HL7 message to Mirth.";
        } else {
            System.err.println("Failed to send HL7 message. Status code: " + response.statusCode());
            return "Failed to send HL7 message. Status code: " + response.statusCode();
        }
    }
}