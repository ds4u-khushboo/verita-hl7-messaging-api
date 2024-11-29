package com.example.hl7project.service;

import com.example.hl7project.dto.*;
import com.example.hl7project.model.*;
import com.example.hl7project.repository.*;
import com.example.hl7project.response.MessageResponse;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SIUInboundService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private InboundSIUMessageRepo inboundSIUMessageRepo;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private HL7UtilityService messageProcessingService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NoShowServiceImpl noShowServiceImpl;

    private static final Logger logger = LoggerFactory.getLogger(SIUInboundService.class);

    public Message processMessage(String hl7Message) throws Exception {

        logger.info("Processing message: {}", hl7Message);

        try {
            Map<String, List<String>> hl7Map = messageProcessingService.parseHl7Message(hl7Message);

            List<String> mshSegment = hl7Map.get("MSH");
            if (mshSegment == null) {
                logger.error("MSH segment not found in message: {}", hl7Message);
                throw new Exception("MSH segment not found.");
            }

            List<String> schSegment = hl7Map.get("SCH");
            if (schSegment == null) {
                throw new Exception("SCH segment not found.");
            }

            List<String> pidSegment = hl7Map.get("PID");
            if (pidSegment == null) {
                logger.error("PID segment not found in message: {}", hl7Message);
                throw new Exception("PID segment not found.");
            }
            logger.debug("Extracting patient data from PID segment");
            Map<String, String> patientData = messageProcessingService.extractPatientData(pidSegment);
            Map<String, String> schData = messageProcessingService.extractDataFromSchSegment(schSegment);
            Map<String, String> mshData = messageProcessingService.extractDataFromMshSegment(mshSegment);

            String messageType = mshData.get("messageType");
            System.out.println("messageType:::" + messageType);
            Long appointmentId = Long.valueOf(schData.get("Visit/Appointment ID"));
            System.out.println("appointmentId:::" + appointmentId);
            String patientPhone = patientData.get("Home Phone Number");
            System.out.println("patientPhone:::" + patientPhone);
            String patientName = patientData.get("Patient Name");
            System.out.println("patientName:::" + patientName);

            // patientService.savePatientData(patientData);
            logger.debug("Message type: {}, Appointment ID: {}, Patient Phone: {}", messageType, appointmentId, patientPhone);
            String noshowMessage = String.format("Your appointment was missed on %s at %s. Appointment ID: %s.",
                    schData.get("Appointment Date"), schData.get("Appointment Time"), appointmentId);

            switch (messageType) {
                case "SIU^S12":
                    logger.info("Processing SIU^S12 message: Appointment Scheduling.");
                    System.out.println("appointmentId:::" + appointmentId);
                    if (appointmentId == null) {
                        logger.error("Appointment ID is null. Cannot process the appointment.");
                        break;
                    }

                    boolean appointmentOptional = appointmentRepository.existsByVisitAppointmentId(appointmentId);
                    System.out.println("appointmentOptional::::" + appointmentOptional);
//                    Patient patient=
//                    System.out.println("appointmentId"+appointmentId);
                    if (appointmentOptional == false) {

                        System.out.println("appointmentOptional" + appointmentOptional);

                        appointmentService.saveAppointmentData(schData, mshData, patientData);
                        updateFirstAppointmentIsConfirmRequestSent(String.valueOf(appointmentId));
                        String smsMessage = String.format("Your appointment is scheduled for %s at %s. Appointment ID: %s",
                                schData.get("Appointment Date"), schData.get("Appointment Time"), appointmentId);
                        notificationService.sendAppointmentNotification(patientPhone, smsMessage);
                        logger.info("Appointment scheduled and notification sent for Appointment ID: {}", appointmentId);
                        messageService.saveMessageEntity(messageType, hl7Message, patientPhone, String.valueOf(appointmentId), "");
                        updateFirstAppointmentIsConfirmRequestSent(String.valueOf(appointmentId));
//                        if(appointmentRepository.findByPatientAndAppointmentDate(patientName,schData.get("Appointment Date")))
                    } else {
                        logger.error("Appointment not found for Appointment ID: {}", appointmentId);
                    }
                    break;

                case "SIU^S14":
                    logger.info("Processing SIU^S14 message: Appointment No-Show.");
                    Appointment appointment = appointmentRepository.findByVisitAppointmentId(appointmentId);
                    if (appointment != null && appointment.getVisitStatusCode() != "N/S") {
//                        Appointment noShowAppointment = appointment;
                        System.out.println("appointmentOptional" + appointment);
                        if (appointment.getVisitStatusCode().equals("PEN")) {
//                            schedulerService.sendNoShowAppointmentMessages();
//                            notificationService.sendNoShowNotification(patientPhone,noshowMessage);
//                            logger.info("Appointment is already marked as No-Show. No further action needed for Appointment ID: {}", appointmentId);
//                        } else {
                            appointment.setVisitStatusCode("N/S");  // Update the visit status to No-Show
                            appointmentRepository.save(appointment);  // Save the updated appointment
                            System.out.println("saved app");
                            updateFirstAppointmentIsConfirmRequestSent(String.valueOf(appointmentId));
                            // Log the status update
                            logger.info("Appointment ID: {} marked as No-Show.", appointmentId);

                            // Step 4: Send the No-Show message to the patient

                            // Save the message to the database (if applicable)
                            messageService.saveMessageEntity(messageType, hl7Message, patientPhone, String.valueOf(appointmentId), "");

                            // Send the No-Show message (using your existing service method)
                            noShowServiceImpl.sendNoShowMessage(patientName, String.valueOf(appointmentId));

                            // Optionally, send additional notifications if necessary (uncomment if needed)
                            notificationService.sendNoShowNotification(patientPhone, noshowMessage);

                            // Optionally, call any other method after the message is sent
                            schedulerService.noshowScheudler();

                        }
                    } else {
                        logger.warn("No appointment found with Appointment ID: {}", appointmentId);
                    }
                    break;

                default:
                    logger.error("Unknown message type: {}", messageType);
                    throw new Exception("Unknown message type: " + messageType);
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", hl7Message, e);  // Log error with stack trace
            throw e;
        }
        return null;
    }

    public String sendNoShowAppointmentMessages() {
        List<AppointmentTextMessageDTO> list = getAppointmentTextMessageDTO();
        System.out.println("Scheduled task started: " + LocalDateTime.now());

        for (AppointmentTextMessageDTO appointment : list) {
            System.out.println("appointment.getAppointmentDate()" + appointment.getAppointmentDate());
            Patient patient = patientRepository.findByExternalPatientId(appointment.getExternalPatientId());
            System.out.println("Processing appointment for patient: " + patient.getName() + " on " + appointment.getAppointmentDate());
//            long daysSinceAppointment = ChronoUnit.DAYS.between(appointment.getAppointmentDate(), LocalDate.now());

//            System.out.println("patient.getAdditionalPhone()" + patient.getAdditionalPhone());
            if (appointment.getTypeCode() == null) {
                noShowServiceImpl.sendNoShowMessage(patient.getName(), appointment.getVisitAppointmentId().toString());
            } else if (appointment.getTypeCode().equals("NS") && appointment.getDays() > 14) {
                noShowServiceImpl.sendNoShowReminderMessage(patient.getName(), patient.getHomePhone(), appointment.getAppointmentDate(), String.valueOf(appointment.getVisitAppointmentId()), appointment.getTextMessageId());
            } else if (appointment.getTypeCode().equals("NSR1") && appointment.getDays() > 28) {
                noShowServiceImpl.sendNoShowReminderMessage(patient.getName(), patient.getHomePhone(), appointment.getAppointmentDate(), String.valueOf(appointment.getVisitAppointmentId()), appointment.getTextMessageId());
            }
        }
        return "success";
    }

    private List<AppointmentTextMessageDTO> getAppointmentTextMessageDTO() {
        List<Object[]> results = appointmentRepository.findNoShowAppointmentsToSendTextMessages();
//        System.out.println("results"+results);
//        return results;
        List<AppointmentTextMessageDTO> reminders = new ArrayList<>();
        for (Object[] row : results) {
            AppointmentTextMessageDTO dto = new AppointmentTextMessageDTO();
            Object value = row[0];
            if (value instanceof Long) {
                dto.setVisitAppointmentId((Long) value);
            } else if (value instanceof String) {
                // If it's a String, convert it to Long
                dto.setVisitAppointmentId(Long.valueOf((String) value));
            } else {
                // Handle the case where the value is neither Long nor String
                throw new IllegalArgumentException("Expected Long or String, but got: " + value.getClass());
            }
            dto.setExternalPatientId((String) row[1]);
            dto.setVisitStatusCode((String) row[3]);
            dto.setTextMessageId(row[4] != null ? Long.parseLong(row[4].toString()) : null);
            dto.setTypeCode(row[5] != null ? (String) row[5] : null);
            dto.setCreatedAt(row[6] != null ? ((java.sql.Timestamp) row[6]).toLocalDateTime() : null);
            dto.setDays(Long.valueOf((Integer) row[7]));
            // Add the DTO to the list
            reminders.add(dto);
            System.out.println("dto" + dto);
        }
        return reminders;
    }

    private void updateFirstAppointmentIsConfirmRequestSent(String appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findByVisitAppointmentId(Long.valueOf(appointmentId));
            if (appointment != null) {
                appointment.setConfirmRequestSent(true);
                appointmentRepository.save(appointment);
                logger.info("Updated is_confirm_request_sent for Appointment ID: {}", appointmentId);
            } else {
                logger.warn("No appointment found with ID: {}", appointmentId);
            }
        } catch (Exception e) {
            logger.error("Error updating confirmation request flag for Appointment ID: {}", appointmentId, e);
        }
    }

//    @Scheduled(cron = "0 11 17 * * ?")
//    public String sendNoShowAppointmentMessages() {
//        List<AppointmentTextMessageDTO> list = getAppointmentTextMessageDTO();
//        System.out.println("Scheduled task started: " + LocalDateTime.now());
//
//        for (AppointmentTextMessageDTO appointment : list) {
//            Patient patient = patientRepository.findByExternalPatientId(appointment.getExternalPatientId());
//            System.out.println("Processing appointment for patient: " + patient.getName() + " on " + appointment.getAppointmentDate());
//
//            if (appointment.getTypeCode() == null) {
//                noShowServiceImpl.sendNoShowMessage(patient.getName(), appointment.getVisitAppointmentId().toString());
//            } else if (appointment.getTypeCode().equals("NS") && appointment.getDays() > 14) {
//                noShowServiceImpl.sendNoShowReminderMessage(patient.getName(), appointment.getAppointmentDate(), patient.getAdditionalPhone(), appointment.getTextMessageId());
//            } else if (appointment.getTypeCode().equals("NSR1") && appointment.getDays() > 28) {
//                noShowServiceImpl.sendNoShowReminderMessage(patient.getName(), appointment.getAppointmentDate(), patient.getAdditionalPhone(), appointment.getTextMessageId());
//            }
//        }
//        return "success";
//    }

//    private List<AppointmentTextMessageDTO> getAppointmentTextMessageDTO() {
//        List<Object[]> results = appointmentRepository.findNoShowAppointmentsToSendTextMessages();
//
//        List<AppointmentTextMessageDTO> reminders = new ArrayList<>();
//        for (Object[] row : results) {
//            AppointmentTextMessageDTO dto = new AppointmentTextMessageDTO();
//            Object value = row[0];
//            if (value instanceof Long) {
//                dto.setVisitAppointmentId((Long) value);
//            } else if (value instanceof String) {
//                // If it's a String, convert it to Long
//                dto.setVisitAppointmentId(Long.valueOf((String) value));
//            } else {
//                // Handle the case where the value is neither Long nor String
//                throw new IllegalArgumentException("Expected Long or String, but got: " + value.getClass());
//            }            dto.setExternalPatientId((String) row[1]);
//            dto.setVisitStatusCode((String) row[3]);
//            dto.setTextMessageId(row[4] != null ? Long.parseLong(row[4].toString()) : null);
//            dto.setTypeCode(row[5] != null ? (String) row[5] : null);
//            dto.setCreatedAt(row[6] != null ? ((java.sql.Timestamp) row[6]).toLocalDateTime() : null);
//            dto.setDays(Long.valueOf((Integer) row[7]));
//            // Add the DTO to the list
//            reminders.add(dto);
//            System.out.println("dto" + dto);
//        }
//        return reminders;
//    }

    @Transactional
    public List<InboundHL7Message> deleteMessage(LocalDate date) {
        logger.info("Deleting messages before the date: {}", date);
        List<InboundHL7Message> deletedAppointments = inboundSIUMessageRepo.deleteByCreatedAtBefore(date);
        logger.info("Deleted {} message(s).", deletedAppointments.size());
        return deletedAppointments;
    }

    @Transactional
    public List<InboundHL7Message> deleteMessagesOlderThanDays(int days) {
        LocalDate cutoffDate = LocalDate.now().minus(days, ChronoUnit.DAYS);
        Timestamp timestamp = Timestamp.valueOf(cutoffDate.atStartOfDay());
        inboundSIUMessageRepo.deleteAllByCreatedAt(timestamp.toLocalDateTime());
        System.out.println("Deleted messages older than " + days + " days (cutoff date: " + cutoffDate + ")");
        return null;
    }

    public MessageResponse getMessagesInRange(String startDate, String endDate) {
        try {
            logger.debug("Fetching messages in range: {} to {}", startDate, endDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
            Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

            List<InboundHL7Message> messages = inboundSIUMessageRepo.findByCreatedAtBetween(startTimestamp, endTimestamp);
            long count = messages.size();

            logger.info("Found {} messages in the date range.", count);
            return new MessageResponse(messages, count);
        } catch (Exception e) {
            logger.error("Error fetching messages in range: {} to {}", startDate, endDate, e);
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd'T'HH:mm:ss.");
        }
    }

    public ResponseEntity<Long> noOfMessage(String messageType) {
        logger.debug("Fetching count of messages for type: {}", messageType);
        long count = inboundSIUMessageRepo.countByMessageType(messageType);
        return ResponseEntity.ok(count);
    }


    public long getTotalAppointmentsCount() {
        logger.debug("Fetching total appointments count.");
        return inboundSIUMessageRepo.countByMessageType("SIU_S12");
    }

    public List<Object[]> getCountByMessageType() {
        logger.debug("Fetching count of messages by message type.");
        return inboundSIUMessageRepo.countMessagesByType();
    }

}
