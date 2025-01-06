package com.example.hl7project.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HL7MessageBuilderService {

    public String buildAdtMessage(String messageType, Map<String, Object> patientData) {
        StringBuilder hl7Message = new StringBuilder();

        hl7Message.append("MSH|^~\\&|ECW|ECW|ECW|ECW|")
                .append(getCurrentTimestamp())
                .append("||")
                .append(messageType)
                .append("|")
                .append(generateUniqueControlID())
                .append("|P|2.4||||\n");

        hl7Message.append("PID|1||")
                .append(patientData.getOrDefault("mrn", ""))
                .append("^^^YourFacilityID^MR||")
                .append(patientData.getOrDefault("lastName", ""))
                .append("^")
                .append(patientData.getOrDefault("firstName", ""))
                .append("||")
                .append(patientData.getOrDefault("dateOfBirth", ""))
                .append("|")
                .append("U|")
                .append(patientData.getOrDefault("address", ""))
                .append("||")
                .append(patientData.getOrDefault("phoneNumber", ""))
                .append("|||||||||YourSSN\n");

        hl7Message.append("PV1|1|O|||||||||||||||||||||||||||||||||||||||||");

        return hl7Message.toString();

    }

    private String getCurrentTimestamp() {
        return java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(java.time.LocalDateTime.now());
    }

    private String generateUniqueControlID() {
        return java.util.UUID.randomUUID().toString();
    }

}
