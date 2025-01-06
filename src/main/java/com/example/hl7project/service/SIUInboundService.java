package com.example.hl7project.service;

import com.example.hl7project.configuration.TextMessageConfig;
import com.example.hl7project.model.*;
import com.example.hl7project.repository.*;
import com.example.hl7project.response.MessageResponse;
import com.example.hl7project.utility.Utility;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private HL7UtilityService messageProcessingService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentConfirmationService appointmentConfirmationService;

    @Autowired
    private Utility utility;

    @Autowired
    private TextMessageConfig textMessageConfig;

    @Autowired
    private NotificationService notificationService;

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

            List<String> pv1Segment = hl7Map.get("PV1");
            if (pv1Segment == null) {
                logger.error("PV1 not found in message: {}", hl7Message);
                throw new Exception("PV1 segment not found.");
            }

            List<String> aigSegment = hl7Map.get("AIG");
            if (aigSegment == null) {
                logger.error("AIG not found in message: {}", hl7Message);
                throw new Exception("AIG segment not found.");
            }
            List<String> ailSegment = hl7Map.get("AIL");
            if (ailSegment == null) {
                logger.error("AIL not found in message: {}", hl7Message);
                throw new Exception("AIL segment not found.");
            }

            logger.debug("Extracting patient data from PID segment");
            Map<String, String> patientData = messageProcessingService.extractPatientData(pidSegment);
            Map<String, String> schData = messageProcessingService.extractDataFromSchSegment(schSegment);
            Map<String, String> mshData = messageProcessingService.extractDataFromMshSegment(mshSegment);
            Map<String, String> pv1Data = messageProcessingService.extractDataFromPV1Segment(pv1Segment);
            Map<String, String> aigData = messageProcessingService.extractDataFromAIGSegment(aigSegment);
            Map<String, String> ailData = messageProcessingService.extractDataFromAILSegment(ailSegment);

            String messageType = mshData.get("messageType");
            Long appointmentId = Long.valueOf(schData.get("Visit/Appointment ID"));
            String patientId = patientData.get("External Patient ID");
            String patientName = patientData.get("Patient Name");
            String patientPhone = patientData.get("Home Phone Number");
            String providerId = pv1Data.get("providerId");
            String providerName = pv1Data.get("LastName") + "," + pv1Data.get("FirstName");

            logger.debug("Message type: {}, Appointment ID: {}, Patient Phone: {}", messageType, appointmentId, patientPhone);
            String noshowMessage = String.format(textMessageConfig.getAppNoShow(),
                    schData.get("Appointment Date"), appointmentId);

            switch (messageType) {
                case "SIU^S12":
                    logger.info("Processing SIU^S12 message: Appointment Scheduling.");
                    if (appointmentId == null) {
                        logger.error("Appointment ID is null. Cannot process the appointment.");
                        break;
                    }

                    boolean appointmentOptional = appointmentRepository.existsByVisitAppointmentId(appointmentId);
                    System.out.println("appointmentOptional::::" + appointmentOptional);
                    if (appointmentOptional == false) {
                        appointmentService.saveAppointmentData(schData, pv1Data, aigData, mshData, patientData);
                        System.out.println("aigData.get(HL7 ID):::" + aigData.get("HL7 ID"));
                        if (aigData.get("HL7 ID") != null) {
                            appointmentService.saveResourceFromAIGSegment(aigSegment);
                        }
                        System.out.println("ailData.get(Location HL7Id):::" + ailData.get("Location HL7Id"));

                        if (ailData.get("Location HL7Id") != null) {
                            appointmentService.saveLocationFromAILSegment(ailSegment);
                        }
                        appointmentService.checkAndUpdateSameSpecialtyNoShowAppointment(patientId, providerName);
                        String smsMessage = String.format(textMessageConfig.getAppCreation(),
                                patientData.get("Patient Name"), utility.hl7DateToDateTime(schData.get("Appointment Date")), appointmentId);
                        appointmentConfirmationService.checkTimeDifferenceAndSendMessage(patientData.get("External Patient ID"), patientPhone);
                        notificationService.sendAppointmentNotification(smsMessage, patientPhone);
                        logger.info("Appointment scheduled and notification sent for Appointment ID: {}", appointmentId);
                        messageService.saveMessageEntity(messageType, hl7Message, smsMessage, patientPhone, String.valueOf(appointmentId), "");
                        updateFirstAppointmentIsConfirmRequestSent(String.valueOf(appointmentId));
                    } else {
                        logger.error("Appointment not found for Appointment ID: {}", appointmentId);
                    }

                    break;

                case "SIU^S14":
                    logger.info("Processing SIU^S14 message: Appointment No-Show.");
                    Appointment appointment = appointmentRepository.findByVisitAppointmentId(appointmentId);
                    if (appointment != null && appointment.getVisitStatusCode() != "N/S") {
                        System.out.println("appointmentOptional" + appointment);
                        if (appointment.getVisitStatusCode().equals("PEN") || appointment.getVisitStatusCode().equals("N/S")) {

                            appointment.setVisitStatusCode("N/S");
                            appointmentRepository.save(appointment);
                            appointment.setVisitStatusCode("N/S");
                            appointment.setAppointmentDate(utility.hl7DateToDateTime(schData.get("Appointment Date")));
                            appointment.setAppointmentDateStr(schData.get("Appointment Date"));
                            appointmentRepository.save(appointment);
                        }

                        updateFirstAppointmentIsConfirmRequestSent(String.valueOf(appointmentId));

                        logger.info("Appointment ID: {} marked as No-Show.", appointmentId);

                        messageService.saveMessageEntity(messageType, hl7Message, noshowMessage, patientPhone, String.valueOf(appointmentId), "");

                        appointmentService.sendNoShowAppointmentMessages();

                    } else {
                        logger.warn("No appointment found with Appointment ID: {}", appointmentId);
                    }
                    break;
                case "SIU^S22":
                    appointmentService.deleteAppointment(appointmentId);
                    logger.info("Appointment ID: {} is deleted.", appointmentId);
                    System.out.println("the appointment is deleted");
                    break;
                default:
                    logger.error("Unknown message type: {}", messageType);
                    throw new Exception("Unknown message type: " + messageType);
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", hl7Message, e);
            throw e;
        }
        return null;
    }


    private void updateFirstAppointmentIsConfirmRequestSent(String appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findByVisitAppointmentId(Long.valueOf(appointmentId));
            if (appointment != null) {
                appointment.setIsConfirmRequestSent(true);
                appointmentRepository.save(appointment);
                logger.info("Updated is_confirm_request_sent for Appointment ID: {}", appointmentId);
            } else {
                logger.warn("No appointment found with ID: {}", appointmentId);
            }
        } catch (Exception e) {
            logger.error("Error updating confirmation request flag for Appointment ID: {}", appointmentId, e);
        }
    }

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
