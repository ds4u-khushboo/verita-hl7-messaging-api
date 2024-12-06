package com.example.hl7project.service;

import com.example.hl7project.dto.PatientRequest;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataInput;
import java.util.Map;

@Component
public class HL7MessageBuilderService {


    @Autowired
    private PatientRepository patientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String buildAdtMessage(String messageType, Map<String, Object> patientData) {
        StringBuilder hl7Message = new StringBuilder();

//        String mrn = (String) patientData.get("mrn");
//        System.out.println("mrn++"+mrn);
//        // Check if the patient already exists by MRN
//        boolean isExistingPatient = patientRepository.existsByExternalPatientId(mrn);
//
//        Patient patient = null;
//
//        if (isExistingPatient)
//            // Retrieve the existing patient data
//            patient = patientRepository.findByPatientId(mrn);
//            System.out.println("patient::::" + patient);

            // Build MSH segment
            hl7Message.append("MSH|^~\\&|ECW|ECW|ECW|ECW|")
                    .append(getCurrentTimestamp())
                    .append("||")
                    .append(messageType)
                    .append("|")
                    .append(generateUniqueControlID())
                    .append("|P|2.4||||\n");

            // Build PID segment
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

            // PV1 Segment (Optional)
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

    private String getString(Map<String, Object> data, String key) {
        return data.getOrDefault(key, "").toString();
    }
}
