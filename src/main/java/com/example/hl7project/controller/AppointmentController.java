package com.example.hl7project.controller;

import com.example.hl7project.dto.AppointmentPatientDTO;
import com.example.hl7project.dto.AppointmentRequest;
import com.example.hl7project.model.InboundHL7Message;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.InboundSIUMessageRepo;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.response.MessageResponse;
import com.example.hl7project.service.*;
import com.example.hl7project.utility.Utility;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/hl7")
public class AppointmentController {


    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ReportService reportService;

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
            // Generate the HL7 SIU message from the request
            String hl7Message = outboundService.processAppointmentRequest(appointmentRequest);
            //  String hl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
            if (hl7Message == null || hl7Message.isEmpty()) {
                throw new IllegalArgumentException("HL7 message cannot be null or empty");
            }

//            System.out.println("Sending HL7 message: " + hl7Message);
//
//            HttpClient httpClient = HttpClient.newBuilder()
//                    .followRedirects(HttpClient.Redirect.ALWAYS)
//                    .build();
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(textMessageConfig.getMirthOutboundSIUEndpoint()))
//                    .header("Content-Type", "text/plain")
//                    .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 302) {
//                response.headers().firstValue("Location").ifPresent(redirectUrl ->
//                        System.out.println("Redirecting to: " + redirectUrl));
//            } else if (response.statusCode() == 200) {
//                System.out.println("Request successful!");
//            } else {
//                System.out.println("Failed with status code: " + response.statusCode());
//            }
//
//            System.out.println("Response body: " + response.body());
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing appointment: " + e.getMessage());
        }
        return ResponseEntity.status(200).body("processed");

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
    public long getNoShowAppointmentCount(@RequestParam(name = "patientId", required = false) String patientId,
                                          @RequestParam(name = "startDate", required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                          @RequestParam(name = "endDate", required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return appointmentService.getNoShowAppointmentsCount(patientId, startDate, endDate);
    }

    @GetMapping("/findNoShowAppointmentsWithPatients")
    public List<AppointmentPatientDTO> findAppointmentsWithPatients(@RequestParam(name = "startDate", required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                    @RequestParam(name = "endDate", required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                    @RequestParam(name = "patientId", required = false) String patientId) {


        List<Object[]> appointments = appointmentService.getNoShowAppointmentDetailsWithPatient(startDate, endDate, patientId);


        List<AppointmentPatientDTO> list = new ArrayList<>();
        Field[] fields = AppointmentPatientDTO.class.getDeclaredFields();

        for (Object[] dataFields : appointments) {
            Map<String, Object> fieldMap = new HashMap<>();
            for (int i = 0; i < dataFields.length; i++) {
                if (i < fields.length) {
                    fieldMap.put(fields[i].getName(), dataFields[i]);
                }
            }
            list.add(new AppointmentPatientDTO());
        }
        return list;
    }

    @GetMapping("/findNewAppointmentsWithPatients")
    public List<AppointmentPatientDTO> findNewAppointmentsWithPatients(@RequestParam(name = "startDate", required = false)
                                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                       @RequestParam(name = "endDate", required = false)
                                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                       @RequestParam(name = "patientId", required = false) String patientId) {

        List<Object[]> appointments = appointmentService.getNewAppointmentDetailsWithPatient(startDate, endDate, patientId);

        List<AppointmentPatientDTO> list = new ArrayList<>();
        Field[] fields = AppointmentPatientDTO.class.getDeclaredFields();

        for (Object[] dataFields : appointments) {
            Map<String, Object> fieldMap = new HashMap<>();
            for (int i = 0; i < dataFields.length; i++) {
                if (i < fields.length) {
                    fieldMap.put(fields[i].getName(), dataFields[i]);
                }
            }
            list.add(new AppointmentPatientDTO());
        }
        return list;
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

        // Set default start and end dates if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Fetch raw results from the service layer
        List<Object[]> appointments = reportService.getNoShowAppointmentsBySpecialty(providerId, specialityName, startDate, endDate);

        // Map raw results to DTOs
        List<AppointmentPatientDTO> dtos = Utility.mapToDto(appointments, AppointmentPatientDTO.class);

        // Log or print the DTOs for debugging (optional)
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


