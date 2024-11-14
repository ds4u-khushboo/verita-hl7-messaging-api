package com.example.hl7project.controller;

import com.example.hl7project.dto.MessageDTO;
import com.example.hl7project.model.MessageEntity;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.MessageEntityRepo;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.response.MessageResponse;
import com.example.hl7project.service.AppointmentScheduler;
import com.example.hl7project.service.AppointmentService;
import com.example.hl7project.service.NoShowService;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hl7")
public class AppointmentController {


    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MessageEntityRepo messageEntityRepo;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentScheduler appointmentScheduler;

    @Autowired
    private NoShowService noShowService;
    @PostMapping("/SIU")
    public Message sendMessge(@RequestBody String hl7mesage) throws Exception {

        return appointmentService.processMessage(hl7mesage);
    }
    @PostMapping("/sendSMs")
    public ResponseEntity<String> sendSMS(@RequestBody MessageDTO message) {
        return appointmentService.getSmsConfirm(message);
    }

    @GetMapping("/multipleApp")
    public String getMultiple(){
       noShowService.checkAppointmentConfirmations() ;


        return "multiple appointment come";
    }
    @RequestMapping("/listByName")
    public List<Patient> getListByName(@RequestParam String patientName) {
        return patientRepository.findByName(patientName);
    }

    @RequestMapping("/listByPhNumber")
    public List<Patient> getListByPhNumber(@RequestParam String phNumber) {
        List<Patient> messages = patientRepository.findByPhoneNumber("+" + phNumber);
        return messages;
    }

    @RequestMapping("/listByTimeRange")
    public List<MessageEntity> getMessagesSentInRange(String startTime) {
        return messageEntityRepo.findMessageEntityByAppointment_StartTime(startTime);
    }
    @GetMapping("/trigger-no-show-check")
    public String triggerNoShowCheck() {
        noShowService.checkNoShowAppointments();
        return "No-show appointment check triggered manually.";
    }
    @RequestMapping("/no-show")
    public void getNoShow() {
         noShowService.checkNoShowAppointments();
    }

    @DeleteMapping("/deleteByDate")
    public List<MessageEntity> getDeleteMessageByDate(@RequestParam("date") String dateString) {
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
    public List<MessageEntity> getDeleteMessageByDate(@RequestParam int days) {
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
    @GetMapping("/scheduler")
    public ResponseEntity<Long> getScheduler() {
        return appointmentScheduler.getScheduler();
    }
    @GetMapping("/schedulerr")
    public void getSchedulerr() {
         appointmentScheduler.checkAndSendMessage();
    }
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
        String appointments = appointmentService.sendNoShowReminders();
        return appointments;
    }

//    @GetMapping("/no-shows")
//    public NoShowReportDTO getNoShowReport() {
//        return appointmentService.getNoShowReport();
//    }

    @GetMapping("/count-by-type")
    public List<Object[]> getCountByMessageType() {
        return appointmentService.getCountByMessageType();
    }
}

