package com.example.hl7project.controller;

import com.example.hl7project.dto.AppointmentRequest;
import com.example.hl7project.dto.BookingInfoDTO;
import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.model.Patient;
import com.example.hl7project.model.Resource;
import com.example.hl7project.repository.InboundSIUMessageRepo;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.response.MessageResponse;
import com.example.hl7project.service.*;
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
    private HL7UtilityService hl7UtilityService;

    @Autowired
    private InboundSIUMessageRepo inboundSIUMessageRepo;

    @Autowired
    private SIUInboundService siuInboundService;

    @Autowired
    private SchedulerService schedulerService;

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


//    @GetMapping("/process")
//    public ResponseEntity<String> processMessages(@RequestParam Long patientId) {
//        try {
//            // Trigger message processing
//            twilioTest.processMessage(patientId);
//            return ResponseEntity.ok("Message processing completed successfully.");
//        } catch (Exception e) {
//            // Handle and respond with errors
//            return ResponseEntity.status(500).body("Error during message processing: " + e.getMessage());
//        }
//    }
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
    public MessageResponse getMessageByTimRange(@RequestParam String startDate, @RequestParam String endDate) {
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

    @GetMapping("/process-no-show-reminders")
    public String processNoShowReminder() {
        String appointments = schedulerService.scheduleNoShowAppointmentsReminders();
        return "Scheduled task result: " + appointments;
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
//    @GetMapping("messgaeByStatus")
//    public void getMessages() {
//        try {
//            schedulerService.multipleppoinmentsScheudlerWithStatus();
//            System.out.println("multiple appointment come");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }

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
        return siuInboundService.getCountByMessageType();
    }


    @PostMapping("/book")
    public ResponseEntity<String> bookAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        try {
            // Generate the HL7 SIU message from the request
            String hl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
            if (hl7Message == null || hl7Message.isEmpty()) {
                throw new IllegalArgumentException("HL7 message cannot be null or empty");
            }

            System.out.println("Sending HL7 message: " + hl7Message);  // Log the received message

            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8089/send-message/"))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 302) {
                response.headers().firstValue("Location").ifPresent(redirectUrl ->
                        System.out.println("Redirecting to: " + redirectUrl));
            } else if (response.statusCode() == 200) {
                System.out.println("Request successful!");
            } else {
                System.out.println("Failed with status code: " + response.statusCode());
            }

            System.out.println("Response body: " + response.body());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing appointment: " + e.getMessage());
        }
        return ResponseEntity.status(200).body("processed");

    }
//    @GetMapping("/slots-type")
//    public List<Resource> getSlots(@RequestParam String startTime, @RequestParam String resourceType) {
//        List<Resource> resource1 = appointmentService.calculateAvailableTimeSlots(startTime,resourceType);
//        return resource1;
//    }
}