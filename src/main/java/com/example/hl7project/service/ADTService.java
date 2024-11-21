package com.example.hl7project.service;

import com.twilio.rest.api.v2010.account.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ADTService {

    @Autowired
    private HL7UtilityService messageProcessingService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(SIUInboundService.class);

    private Map<String, List<String>> receiveHl7Message(String hl7Message) {
        Map<String, List<String>> hl7Map = new HashMap<>();
        String[] segments = hl7Message.split("\r");

        for (String segment : segments) {
            String[] fields = segment.split("\\|");
            if (fields.length > 0) {
                hl7Map.put(fields[0], Arrays.asList(fields));
            }
        }

        return hl7Map;
    }


    public Message processMessage(String hl7Message) {
        Map<String, List<String>> hl7Map = receiveHl7Message(hl7Message);


        Map<String, String> mshData = messageProcessingService.extractDataFromMshSegment(hl7Map.get("MSH"));
        Map<String, String> patientData = messageProcessingService.extractPatientData(hl7Map.get("PID"));
//        Map<String, String> schData = messageProcessingService.extractDataFromSchSegment(hl7Map.get("SCH"));

//        List<Appointment> appointments= (List<Appointment>) schData;
        String patientId = patientData.get("Patient ID");
        String patientName = patientData.get("Patient Name");
        String patientPhone = patientData.get("Home Phone Number");

        String messageType = mshData.get("messageType");

        // Determine action based on message type
        switch (messageType) {

            case "ADT^A04":
            case "ADT^A28":
                String smsMessage = String.format("Your have registered at patient named as %s  Patient ID: %s", patientName, patientId);
                patientService.savePatientData(patientData);
                messageService.saveMessageEntity(messageType,hl7Message,patientPhone,"",patientId);
                messageService.saveTextMessage(messageType,patientData);
               // messageService.saveMessageEntity(messageType,smsMessage,hl7Message,patientPhone);
              //  notificationService.sendPatientCreateNotification(patientPhone, smsMessage);
                break;

            case "ADT^A08":
            case "ADT^A31":
                patientService.updatePatientData(patientData);
                System.out.println("patient data is updated");
            default:
                System.out.println("Unknown message type: " + messageType);
                break;
        }
        return null;
    }
}