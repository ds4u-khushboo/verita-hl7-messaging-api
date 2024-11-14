package com.example.hl7project.service;

import com.example.hl7project.configuration.TwilioConfig;
import com.example.hl7project.dto.AppointmentTextMessageDTO;
import com.example.hl7project.dto.MessageDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.MessageEntity;
import com.example.hl7project.model.Patient;
import com.example.hl7project.model.TextMessage;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.MessageEntityRepo;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.repository.TextMessageRepository;
import com.example.hl7project.response.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private TwillioService twillioService;

    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private NoShowService noShowService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageEntityRepo messageEntityRepo;

    @Autowired
    private TextMessageRepository textMessageRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentScheduler appointmentScheduler;

    public Map<String, List<String>> receiveHl7Message(String hl7Message) {
        System.out.println("Received HL7 message: " + hl7Message); // Log the message
        Map<String, List<String>> hl7Map = new HashMap<>();

        // Split by carriage return for HL7 messages
        String[] segments = hl7Message.split("\r");

        for (String segment : segments) {
            String[] fields = segment.split("\\|");
            if (fields.length > 0) {
                hl7Map.put(fields[0], Arrays.asList(fields));
            }
        }

        System.out.println("Parsed segments: " + hl7Map.keySet());
        return hl7Map;
    }

    private Set<String> processedAppointmentIds = new HashSet<>();

    public Message processMessage(String hl7Message) throws Exception {
        Map<String, List<String>> hl7Map = receiveHl7Message(hl7Message);
        List<String> mshSegment = hl7Map.get("MSH");
        if (mshSegment == null) throw new Exception("MSH segment not found.");

        List<String> pidSegment = hl7Map.get("PID");
        if (pidSegment == null) throw new Exception("PID segment not found.");

        List<String> schSegment = hl7Map.get("SCH");
        if (schSegment == null) throw new Exception("SCH segment not found.");

        Map<String, String> patientData = extractPatientDataFromPidSegment(pidSegment);
        Map<String, String> schData = extractDataFromSchSegment(schSegment);
        Map<String, String> mshData = extractDataFromMshSegment(mshSegment);

        String messageType = mshData.get("messageType");
        String appointmentId = schData.get("Visit/Appointment ID");
        String patientPhone = patientData.get("Home Phone Number");

        System.out.println("processedAppointmentIds" + processedAppointmentIds);


        String patientName = patientData.get("Patient Name");
        String appointmentDate = schData.get("Appointment Date");
        String appointmentTime = schData.get("Appointment Time");
        String patientId = patientData.get("Patient ID");
        String smsMessage;
        String noshowMessage = "";
        switch (messageType) {
            case "SIU^S12":
//                if (processedAppointmentIds.contains(appointmentId)) {
//                    System.out.println("Duplicate message detected, skipping SMS for appointment ID: " + appointmentId);
//                    return null;
//                }

                processedAppointmentIds.add(appointmentId);
                if (!appointmentRepository.existsByVisitAppointmentId(appointmentId)) {
                    smsMessage = String.format(twilioConfig.getAppCreation(), patientName, appointmentDate, appointmentTime, appointmentId);
                    saveAppointmentData(schData, mshData, patientData);
                    savePatientData(patientData);
                    saveMessageEntity(hl7Message, smsMessage, patientPhone, messageType, schData, patientData);
                    twillioService.getTwilioService(smsMessage, "+91" + patientPhone);
                    noShowService.checkAppointmentConfirmations();
                    updateFirstAppointmentIsConfirmRequestSent(appointmentId);
                    appointmentScheduler.getScheduler();
                    System.out.println("message sent");
                    System.out.println("smsMessage:::" + smsMessage);
                }
                break;
            case "SIU^S14":
                System.out.println("schData.get(\"Visit Status Code\")::" + schData.get("Visit Status Code"));
                if (schData.get("Visit Status Code").equals("N/S")) {
                    noshowMessage = String.format("Dear %s, we have noticed your appointment was missed on %s at %s. Appointment ID: %s. Please contact us to reschedule.", patientName, appointmentDate, appointmentTime, appointmentId);
                    twillioService.getTwilioService(noshowMessage, "+91" + patientPhone);

                    // Send No-Show reminder through NoShowService
                    noShowService.checkNoShowAppointments(); // Check for 2-week and 4-week reminders
//                    saveTextMessage()
                    saveMessageEntity(hl7Message, noshowMessage, patientPhone, messageType, schData, patientData);

                    // Send SMS to the patient for the No-Show appointment
                    System.out.println("No-show message sent for appointment ID: " + appointmentId);
                } else {

                    smsMessage = String.format("Dear %s, your appointment has been modified for %s at %s. Appointment ID: %s. Tap 'Yes' to confirm or 'No' to refuse.", patientName, appointmentDate, appointmentTime, appointmentId);
                    twillioService.getTwilioService(smsMessage, "+91" + patientPhone);
                    saveMessageEntity(hl7Message, smsMessage, patientPhone, messageType, schData, patientData);
                }
                break;

            case "SIU^S22":
                if (appointmentRepository.existsByVisitAppointmentId(appointmentId)) {
//                    saveAppointmentData(schData,mshData);
//                    savePatientData(patientData);
                    smsMessage = String.format(twilioConfig.getDeletion(), appointmentId, appointmentTime);

                    appointmentRepository.deleteByVisitAppointmentId(appointmentId);
                    saveMessageEntity(hl7Message, smsMessage, patientPhone, messageType, schData, patientData);
                    twillioService.getTwilioService(smsMessage, "+91" + patientPhone);
                    System.out.println("message sent");
                    System.out.println("smsMessage:::" + smsMessage);
                }
                break;
            case "SIU^S1":

            default:
                System.out.println("Unhandled message type: " + messageType);
                return null;
        }

        System.out.println("patientPhone::::" + patientPhone);
//        if (!appointmentRepository.existsByVisitAppointmentId(appointmentId)) {
//            twillioService.getTwilioService(smsMessage, "+91" + patientPhone);
//            System.out.println("Sent SMS for appointment ID: " + appointmentId);
//        }
        return null;
    }

    private void updateFirstAppointmentIsConfirmRequestSent(String appointmentId) {
        try {
            // Fetch the appointment by its visitAppointmentId
            Appointment appointment = appointmentRepository.findByVisitAppointmentId(appointmentId);

            if (appointment != null) {
                // Update the `is_confirm_request_sent` field to `true` (or 1)
                appointment.setConfirmRequestSent(true); // Assuming setter method is setConfirmRequestSent()

                // Save the updated appointment back to the database
                appointmentRepository.save(appointment);

                System.out.println("Updated is_confirm_request_sent to true for first appointment ID: " + appointmentId);
            } else {
                System.out.println("No appointment found with ID: " + appointmentId);
            }
        } catch (Exception e) {
            System.err.println("Error updating first appointment: " + e.getMessage());
        }
    }

    public ResponseEntity<String> getSmsConfirm(MessageDTO messageDTO) {
        String responseMessage = " ";
        MessageDTO body = new MessageDTO();
        System.out.println("body::" + body.getMessage());
        switch (messageDTO.getMessage().toLowerCase()) {
            case "yes":
                responseMessage = "Thank you for confirming your appointment.";
                break;
            case "no":
                responseMessage = "Your appointment has been canceled.";
                break;
            default:
                responseMessage = "Please reply with 'Yes' to confirm or 'No' to cancel your appointment.";
        }

        String twimlResponse = "<Response><Message>" + responseMessage + "</Message></Response>";
        return ResponseEntity.ok()
                .header("Content-Type", "text/xml")
                .body(twimlResponse);
    }

//
//    public void getNoShow(){
//        return messageService.sendNoShowReminder();
//    }
    public String sendNoShowReminders() {
        List<AppointmentTextMessageDTO> list = getAppointmentTextMessageDTO();

        for (AppointmentTextMessageDTO appointment : list) {
            Patient patient = patientRepository.findByExternalPatientId(appointment.getExternalPatientId());
            if (appointment.getTypeCode() == null) {
                messageService.sendNoShowMessage(patient.getName(), patient.getAdditionalPhone(), appointment.getAppointmentDate(), appointment.getVisitAppointmentId().toString());
            }
            else if(appointment.getTypeCode().equals("NS")&&appointment.getDays()>14){
                messageService.sendNoShowReminder(patient.getName(),appointment.getAppointmentDate(),patient.getAdditionalPhone(),appointment.getVisitAppointmentId().toString(),appointment.getTextMessageId());
            }
            else if(appointment.getTypeCode().equals("NSR1")&&appointment.getDays()>28){
                messageService.sendNoShowReminder(patient.getName(),appointment.getAppointmentDate(),patient.getAdditionalPhone(),appointment.getVisitAppointmentId().toString(),appointment.getTextMessageId());

            }
        }
        return "success";
    }

    private List<AppointmentTextMessageDTO> getAppointmentTextMessageDTO(){
        List<Object[]> results = appointmentRepository.findNoShowAppointmentsToSendTextMessages();

        List<AppointmentTextMessageDTO> reminders = new ArrayList<>();
        String json = null;
        for (Object[] row : results) {
            //get No show messages
            AppointmentTextMessageDTO dto = new AppointmentTextMessageDTO();
            dto.setVisitAppointmentId(Long.valueOf((String) row[0]));
            dto.setExternalPatientId((String)row[1]);
            dto.setVisitStatusCode((String) row[3]);
            dto.setTextMessageId(row[4] != null ? Long.parseLong(row[4].toString()) : null);
            dto.setTypeCode(row[5] != null ? (String) row[5] : null);

            // Map created at time (convert to LocalDateTime)
            dto.setCreatedAt(row[6] != null ? ((java.sql.Timestamp) row[6]).toLocalDateTime() : null);
            dto.setDays(Long.valueOf((Integer) row[7]));
            // Add the DTO to the list
            reminders.add(dto);
            System.out.println("dto" + dto);
        }
        return reminders;
    }



            // Convert the list to JSON
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                json = objectMapper.writeValueAsString(reminders);
//                System.out.println(json);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return json;
//        }
//        return json;


        // Convert the list of Object[] to a list of Appointment objects
//        List<Appointment> appointments = new ArrayList<>();
//        for (AppointmentTextMessageDTO appointmentTextMessageDTO: results) {
//            Appointment appointment = new Appointment();
//            appointment.setVisitAppointmentId(appointmentTextMessageDTO.);
//            appointment.setAppointmentDate((String) row[1]);
//            appointment.setVisitStatusCode((String) row[2]);
//            appointment.setTextMessageId(row[3] != null ? Long.parseLong(row[3].toString()) : null);
//            appointment.setTypeCode(row[4] != null ? (String) row[4] : null);
//            appointment.setCreatedAt(row[5] != null ? (String) row[5] : null);
//            appointment.setDays((Integer) row[6]);
//
//            appointments.add(appointment);
//        }

    private Map<String, String> extractPatientDataFromPidSegment(List<String> pidSegment) {
        Map<String, String> patientData = new HashMap<>();
        System.out.println("pidSegment--" + pidSegment);

        System.out.println("patientData" + patientData);
        // Accessing relevant indices based on the provided hl7Map
        patientData.put("Patient ID", (pidSegment.size() > 3) ? pidSegment.get(3) : null);
        patientData.put("External Patient ID", (pidSegment.size() > 4) ? pidSegment.get(4) : null);
        patientData.put("Patient Name", (pidSegment.size() > 5) ? pidSegment.get(5).replaceAll("\\^", " ") : null);
        patientData.put("Date of Birth", (pidSegment.size() > 7) ? pidSegment.get(7) : null);
        patientData.put("Sex", (pidSegment.size() > 8) ? pidSegment.get(8) : null);
        patientData.put("Race", (pidSegment.size() > 10) ? pidSegment.get(10) : null);
        patientData.put("Patient Address", (pidSegment.size() > 11) ? pidSegment.get(11).replaceAll("\\^", ", ") : null);
        patientData.put("Home Phone Number", (pidSegment.size() > 13) ? pidSegment.get(13) : null);
        patientData.put("Additional Phone", (pidSegment.size() > 14) ? pidSegment.get(14) : null);
        patientData.put("Primary Language", (pidSegment.size() > 15) ? pidSegment.get(15) : null);
        patientData.put("Marital Status", (pidSegment.size() > 16) ? pidSegment.get(16) : null);
        patientData.put("Patient Account Number", (pidSegment.size() > 18) ? pidSegment.get(18) : null);
        patientData.put("SSN", (pidSegment.size() > 19) ? pidSegment.get(19) : null);
        patientData.put("Ethnicity", (pidSegment.size() > 22) ? pidSegment.get(22) : null);
        patientData.put("Default Location", (pidSegment.size() > 23) ? pidSegment.get(23) : null);

        System.out.println("Extracted patientData: " + patientData);
        return patientData;
    }

    private Map<String, String> extractDataFromMshSegment(List<String> mshSegment) {
        Map<String, String> mshData = new HashMap<>();
        System.out.println("SCH Segment: " + mshSegment);
        // Accessing relevant indices based on the SCH segment structure
        mshData.put("Segment Type ID", (mshSegment.size() > 0) ? mshSegment.get(0) : null); // SCH.00 - Required
        mshData.put("messageType", (mshSegment.size() > 8) ? mshSegment.get(8) : null); // SCH.08 - Required
        mshData.put("messageDateTime", (mshSegment.size() > 6) ? mshSegment.get(6) : null); // SCH.08 - Required

        System.out.println("mshData" + mshData);
        return mshData;
    }

    private Map<String, String> extractDataFromSchSegment(List<String> schSegment) {
        Map<String, String> schData = new HashMap<>();
        System.out.println("SCH Segment: " + schSegment);
        schData.put("Segment Type ID", (schSegment.size() > 0) ? schSegment.get(0) : null); // SCH.00 - Required
        schData.put("Temp Visit/Appointment ID", (schSegment.size() > 1) ? schSegment.get(1) : null); // SCH.01 - Optional
        schData.put("Visit/Appointment ID", (schSegment.size() > 2) ? schSegment.get(2) : null); // SCH.02 - Optional
        schData.put("Occurrence Number", (schSegment.size() > 3) ? schSegment.get(3) : null); // SCH.03 - Not supported
        schData.put("Placer Group Number", (schSegment.size() > 4) ? schSegment.get(4) : null); // SCH.04 - Not supported
        schData.put("Schedule ID", (schSegment.size() > 5) ? schSegment.get(5) : null); // SCH.05 - Not supported
        schData.put("Event Reason", (schSegment.size() > 6) ? schSegment.get(6) : null); // SCH.06 - Not supported
        schData.put("Appointment Reason", (schSegment.size() > 7) ? schSegment.get(7) : null); // SCH.07 - Optional
        schData.put("Appointment Type", (schSegment.size() > 8) ? schSegment.get(8) : null); // SCH.08 - Required
        schData.put("Appointment Duration", (schSegment.size() > 9) ? schSegment.get(9) : null); // SCH.09 - Required
        schData.put("Appointment Duration Units", (schSegment.size() > 10) ? schSegment.get(10) : null); // SCH.10 - Not supported
        schData.put("Appointment Timing Quantity", (schSegment.size() > 11) ? schSegment.get(11) : null); // SCH.11 - Required
        String appointmentTiming = (schSegment.size() > 11) ? schSegment.get(11) : null;
        if (appointmentTiming != null) {
            // Split the appointment timing into start and end times
            String[] times = appointmentTiming.split("\\^");
            if (times.length > 0) {
                String startTime = times[0]; // Get the start time (20241018163000)

                // Extract date and time from startTime
                String appointmentDate = startTime.substring(0, 8);
                String appointmentHour = startTime.substring(8, 10);
                String appointmentMinute = startTime.substring(10, 12);
                String appointmentSecond = startTime.substring(12, 14);
                String formattedTime = String.format("%s:%s:%s", appointmentHour, appointmentMinute, appointmentSecond);
                // Store in the map
                schData.put("Appointment Date", appointmentDate); // Store date
                schData.put("Appointment Time", formattedTime); // Store time
            }
        }
        schData.put("Resource Name", (schSegment.size() > 20) ? schSegment.get(20) : null); // SCH.20 - Required
        schData.put("Encounter Notes", (schSegment.size() > 24) ? schSegment.get(24) : null); // SCH.24 - Optional
        schData.put("Visit Status Code", (schSegment.size() > 25) ? schSegment.get(25) : null); // SCH.25 - Optional

        System.out.println("Extracted SCH Data:");
        for (Map.Entry<String, String> entry : schData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Extracted SCH Data: " + schData);
        return schData;
    }

    //    @Transactional
    public void savePatientData(Map<String, String> patientData) {
        Patient patient = new Patient();
        patient.setId(patientData.get("Patient ID"));
        patient.setExternalPatientId(patientData.get("External Patient ID"));
        patient.setName(patientData.get("Patient Name"));
        patient.setDateOfBirth(patientData.get("Date of Birth"));
        patient.setSex(patientData.get("Sex"));
        patient.setRace(patientData.get("Race"));
        patient.setAddress(patientData.get("Patient Address"));
        patient.setPhoneNumber(patientData.get("Home Phone Number"));
        patient.setLanguage(patientData.get("Primary Language"));
        patient.setMaritalStatus(patientData.get("Marital Status"));

        System.out.println("patient data saved!!!");

        patientRepository.save(patient);
    }

    public void saveTextMessage(String messageType, Map<String, String> schData) {
        // Extract necessary information from the data
        String appointmentId = schData.get("Visit/Appointment ID");
        String typeCode = messageType.equals("SIU^S14") ? "NS" : "NSR1";  // Default typeCode, change based on conditions
        LocalDateTime currentTime = LocalDateTime.now();  // Record the current time for message creation

        // Create and populate TextMessage entity
        TextMessage textMessage = new TextMessage();
        textMessage.setVisitAppointmentId(appointmentId);
        textMessage.setTypeCode(typeCode);
        textMessage.setCreatedAt(currentTime);

        // Save the text message entity to the database
        textMessageRepository.save(textMessage);
        System.out.println("Text message saved to database for appointment ID: " + appointmentId);
    }


    //    @Transactional
    public void saveAppointmentData(Map<String, String> schData, Map<String, String> mshData, Map<String, String> patientData) {

        Patient patient = patientRepository.findByExternalPatientId(patientData.get("External Patient ID"));
        if (patient == null) {
            // If the patient does not exist, save the patient data first
            savePatientData(patientData); // Save the patient data
            patient = patientRepository.findByExternalPatientId(patientData.get("External Patient ID"));
        }
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(schData.get("Appointment Date"));
        appointment.setAppointmentTime(schData.get("Appointment Time"));
        appointment.setAppointmentReason(schData.get("Appointment Reason"));
        appointment.setVisitStatusCode(schData.get("Visit Status Code"));
        appointment.setResourceName(schData.get("Resource Name"));
        appointment.setAppointmentDatetime(schData.get("Appointment Timing Quantity"));
        appointment.setVisitAppointmentId(schData.get("Visit/Appointment ID"));
        appointment.setDuration(schData.get("Appointment Duration"));
        appointment.setDurationUnits(schData.get("Appointment Duration Units"));
        appointment.setAppointmentType(schData.get("Appointment Type"));
        appointment.setNotes(schData.get("Encounter Notes"));
        appointment.setExternalPatientId(patientData.get("External Patient ID"));
        String messageDateTime = mshData.get("messageDateTime");
        if (messageDateTime != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime createdAt = LocalDateTime.parse(messageDateTime, formatter);
                appointment.setCreatedAt(createdAt);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing messageDateTime: " + messageDateTime);
                e.printStackTrace();
            }
        }
//        appointment.setStartTime();
        System.out.println("appointment data saved!!!");
        // Save the appointment entity
        appointmentRepository.save(appointment);
    }

    public void saveMessageEntity(String message, String hl7Messgae, String phoneNumber, String messageType, Map<String, String> schData, Map<String, String> patientData) {
        String appointmentId = schData.get("Visit/Appointment ID");
        String patientId = patientData.get("External Patient ID");

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setRawMessage(hl7Messgae);
        messageEntity.setMessageText(message);
        messageEntity.setSentAt(LocalDateTime.now());
        messageEntity.setMessageType(messageType);
        messageEntity.setPhoneNumber(phoneNumber);
        messageEntity.setVisitAppointmentId(appointmentId);
        System.out.println("appointmentId" + appointmentId);
        System.out.println("messageEntity" + messageEntity);
        // Save the MessageEntity in the repository
        messageEntityRepo.save(messageEntity);
        System.out.println("Message text data saved for Appointment ID: " + appointmentId);

    }


//    private void handleNoShowNotification(String appointmentId, String startTime) {
//
//        long count = 0;
////        saveMessageEntity(appointmentId, patientId, startTime, messageType, patientName, toNumber, messageBody);
//        List<MessageEntity> existingAppointment = messageEntityRepo.findAll();
//        for (MessageEntity messageEntity : existingAppointment) {
//            Appointment appointment = new Appointment();
//            appointment.setStartTime(startTime);
//            //   appointment.setVisitStatusCode(AppointmentStatus.No_Show);
//            // messageEntityRepo.save(messageEntity);
//            updateAppointmentStatus(appointmentId, appointment.getVisitStatusCode());
//            System.out.println("no show occurred");
//        }
//    }


//        switch (messageType) {
//            case "SIU_S12":
//                String message = String.format(twilioConfig.getAppCreation(), patientName, startTime, location, placerAppointmentID);
//                saveMessageEntity(placerAppointmentID, patientID, startTime, messageType, patientName, phoneNumber, message);
//                return twillioService.getTwilioService(message, phoneNumber);
//            case "SIU_S13":
//                String modifiedMessage = String.format(twilioConfig.getAppModification(), startTime, location);
//                saveMessageEntity(placerAppointmentID, patientID, startTime, messageType, patientName, phoneNumber, modifiedMessage);
//                return twillioService.getTwilioService(modifiedMessage, phoneNumber);
//            case "SIU_S26":
//                String noShowMessage = String.format(twilioConfig.getAppNoShow(), patientName, startTime, location, placerAppointmentID);
//                saveMessageEntity(placerAppointmentID, patientID, startTime, messageType, patientName, phoneNumber, noShowMessage);
//                return twillioService.getTwilioService(noShowMessage, phoneNumber);
//            default:
//                String defaultMessage = "Dear " + patientName + ", your appointment information has been updated. Appointment ID: " + placerAppointmentID;
//                return twillioService.getTwilioService(defaultMessage, phoneNumber);
//        }
//    }

//    List<List<String>> parsedMessage = parseHl7Message(hl7Message);
//
////    String appointmentId = hl7Message.g
//        String patientName = extractPatientName(parsedMessage);
//        String location = extractField(parsedMessage, 1, 15);
//        String startTime = extractStartTime(parsedMessage);
//        String toNumber = extractPhoneNumber(parsedMessage);
//        String messageType = extractMessageType(parsedMessage);
//        String patientId = extractField(parsedMessage, 2, 1);
//        System.out.println("appointmentId::" + appointmentId);
//        System.out.println("messageType::" + messageType);
//        System.out.println("startTime::" + startTime);
//        System.out.println("patientId::" + patientId);
//        System.out.println("toNumber::" + toNumber);

//        AppointmentDetails appointmentDetails = new AppointmentDetails();
//        String messageBody = createMessageBody(messageType, patientName, startTime, location, String.valueOf(appointmentId), appointmentDetails);
//
//        switch (messageType) {
//            case "SIU_S12":
//                handleAppointmentCreation(appointmentId, patientId, patientName, startTime, location, toNumber, messageBody);
//                //  twillioService.getTwilioService(messageBody, toNumber);
//                twillioService.getTwilioService(messageBody, toNumber);
//
//                break;
//
//            case "SIU_S13":
//                handleAppointmentModification(patientName, startTime, location, appointmentId);
//                twillioService.getTwilioService(messageBody, toNumber);
//                break;
//
//            case "SIU_S26":
//                handleNoShowNotification(appointmentId, patientId, startTime, messageType, patientName, toNumber, messageBody);
//                twillioService.getTwilioService(messageBody, toNumber);
//                break;
//            default:
//                break;
//        }

//        return new ResponseEntity<>("Success", HttpStatus.OK);


//    private String createMessageBody(String messageType, String patientName, String startTime, String location, String appointmentId, AppointmentDetails appointmentDetails) {
//        switch (messageType) {
//            case "SIU_S12":
//                // Message for Appointment Creation
//                return String.format(twilioConfig.getAppCreation(), patientName, startTime, location, appointmentId);
//            case "SIU_S26":
//                // Message for No-Show Notification
//                return String.format(twilioConfig.getAppNoShow(), patientName, startTime, location, appointmentId);
//            default:
//                return "Dear " + patientName + ", your appointment information has been updated. Appointment ID: " + appointmentId;
//        }
//    }

//    private void saveMessageEntity(String appointmentId, String patientId, String startTime, String messageType, String patientName, String toNumber, String messageBody) throws Exception {
//        MessageEntity messageEntity = new MessageEntity();
//        Patient patient = new Patient();
////        patient.setPatientId());
//        patient.setPatientId(patientId);
//        patient.setPatientName(patientName);
//        patient.setPatientPhone(toNumber);
//
//        patientRepository.save(patient);
//        Appointment newAppointment = new Appointment();
//        appointmentRepository.findByPlacerAppointmentId(appointmentId)
//                .ifPresentOrElse(appointment -> {
//                    appointment.setPlacerAppointmentId(appointmentId);
//                    appointment.setStartTime(startTime);
//                    appointment.setPatient(patient);
//                    appointmentRepository.save(appointment);
//                }, () -> {
//                    newAppointment.setPlacerAppointmentId(null);
//                    newAppointment.setStartTime(startTime);
//                    newAppointment.setPatient(patient);
//                    appointmentRepository.save(newAppointment);
//                });
////        } else {
////            // Update existing appointment if needed
////           return "appointknet is there";
////        }
////        MessageEntity messageEntity1 = messageEntityRepo.fin(patient);
//        MessageEntity messageEntity1 = messageEntityRepo.findByAppointment(newAppointment);
////                .orElseThrow(() -> new RuntimeException("Appointment not found for ID: " + appointmentId));
//
//        if (messageEntity1 == null) {
////        System.out.println("appointments" + messageEntity1.getPlacerAppointmentId());
////        if(appointments!=null) {
//            messageEntity.setPatient(patient);
//            messageEntity.setMessageText(messageBody);
//            messageEntity.setMessageType(messageType);
//            messageEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
////            messageEntity.setAppointment(appointment);
//            messageEntityRepo.save(messageEntity);
//        }
//    }

    @Transactional
    public List<MessageEntity> deleteMessage(LocalDate date) {
        List<MessageEntity> deletedAppointments = messageEntityRepo.deleteByCreatedAtBefore(date);
        if (!deletedAppointments.isEmpty()) {
            System.out.println("Data deleted successfully. Number of records deleted: " + deletedAppointments.size());
        } else {
            System.out.println("No records found for deletion before the specified date.");
        }
        return deletedAppointments;
    }

    @Transactional
    public List<MessageEntity> deleteMessagesOlderThanDays(int days) {
        LocalDate cutoffDate = LocalDate.now().minus(days, ChronoUnit.DAYS);
        Timestamp timestamp = Timestamp.valueOf(cutoffDate.atStartOfDay());
        messageEntityRepo.deleteAllBySentAt(timestamp.toLocalDateTime());
        System.out.println("Deleted messages older than " + days + " days (cutoff date: " + cutoffDate + ")");
        return null;
    }

    public MessageResponse getMessagesInRange(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
            Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

            // Query the repository
            List<MessageEntity> messages = messageEntityRepo.findByCreatedAtBetween(startTimestamp, endTimestamp);
            long count = messages.size();

            return new MessageResponse(messages, count);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd'T'HH:mm:ss.");
        }
    }

    public ResponseEntity<Long> noOfMessage(String messageType) {
        //  List<MessageEntity> allMessages = messageEntityRepo.findAll();
        long count = messageEntityRepo.countByMessageType(messageType);
        return ResponseEntity.ok(count);
    }

    private String handleAppointmentCreation(String appointmentId, String patientID, String messageType, String patientName, String toNumber, String messageBody, String startTime) {
        if (!appointmentRepository.existsById(Long.valueOf(appointmentId))) {
            try {
                // Send SMS Notification for Appointment Creation
//                Message message = twillioService.getTwilioService(messageBody, toNumber);
                System.out.println(toNumber);
                // Save the Appointment and Message Entities
//                saveMessageEntity(appointmentId, patientID, startTime, messageType, patientName, toNumber, messageBody);
                return "Dear " + patientName + ", your appointment is scheduled" + "at" + startTime;

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error processing SIU_S12 message", e);
            }
        } else {
            System.out.println("Appointment already exists: " + appointmentId);
        }
        return "Appointment already exists: " + appointmentId;
    }

    private String handleAppointmentModification(String patientName, String startTime, String location, String appointmentId) {
        // Fetch existing appointment details
        Optional<Appointment> existingAppointment = appointmentRepository.findById(Long.valueOf(appointmentId));

        if (existingAppointment != null) {
            Appointment appointment = existingAppointment.get();

            // Update the appointment details
//            appointment.setStartTime(startTime);
//            appointment.setLocation(location);
            appointmentRepository.save(appointment);

            return "Dear " + patientName + ", your appointment reschedule information processed.";
        } else {
            // Handle the case where the appointment doesn't exist
            return "Dear " + patientName + ", your appointment reschedule information could not be processed. Please contact support.";
        }
    }

    public long getTotalAppointmentsCount() {
        return messageEntityRepo.countByMessageType("SIU_S12");  // Assuming SIU_S12 represents appointment creation
    }

//    public HashMap<String, String> getAppointmentDetails() {
//        List<Appointment> appointments = appointmentRepository.findAll();
//        HashMap<String, String> hashMap = new HashMap<>();
//
//        for (Appointment appointment : appointments) {
//            hashMap.put("location", appointment.getLocation());
//            hashMap.put("start_Time", String.valueOf(appointment.getStartTime()));
//
//        }
//        return hashMap;
//    }

//    public NoShowReportDTO getNoShowReport() {
//        List<Appointment> result = appointmentRepository.findAll();
//        List<NoShowReportDTO.NoShowAppointmentDTO> noShowAppointments = new ArrayList<>();
//
//        for (Appointment row : result) {
//            Appointment appointment = new Appointment();
////            MessageEntity messageEntity = (MessageEntity) row[1];
//
//            NoShowReportDTO.NoShowAppointmentDTO dto = new NoShowReportDTO.NoShowAppointmentDTO(
//                    appointment.getAppointmentId(),
//                    appointment.getStartTime(),
//                    appointment.getLocation(),
//                    appointment.getStatus()
////                    messageEntity.getPatientName(),
////                    messageEntity.getPhNumber()
//            );
//
//            noShowAppointments.add(dto);
//        }

//        long noShowCount = appointmentRepository.countNoShowAppointments();
//        System.out.println("No-Show Appointments: " + result.size());
//        System.out.println("No-Show Count: " + noShowCount);
//        return new NoShowReportDTO(noShowAppointments, noShowCount);
//    }

//    public String getAllPatient(String type) {
//        List<MessageEntity> messageEntities = messageEntityRepo.findByMessageType(type);
//        for (MessageEntity message : messageEntities) {
//            return message.getPatient().getPatientName();
//        }
//
//        return null;
//    }

    public List<Object[]> getCountByMessageType() {
        return messageEntityRepo.countMessagesByType();
    }

    private List<List<String>> parseHl7Message(String hl7Message) {
        List<List<String>> parsedMessage = new ArrayList<>();
        String[] lines = hl7Message.split("\r");
        for (String line : lines) {
            String[] fields = line.split("\\|");
            List<String> fieldList = new ArrayList<>(Arrays.asList(fields));
            parsedMessage.add(fieldList);
        }
        return parsedMessage;
    }

    private String getMessageType(List<List<String>> parsedMessage) {
        if (parsedMessage.isEmpty() || parsedMessage.size() < 1) {
            return null;
        }
        return parsedMessage.get(0).get(0);
    }

    private String extractField(List<List<String>> parsedMessage, int segmentIndex, int fieldIndex) {
        if (parsedMessage.size() > segmentIndex && parsedMessage.get(segmentIndex).size() > fieldIndex) {
            return parsedMessage.get(segmentIndex).get(fieldIndex);
        }
        return "";
    }
}
