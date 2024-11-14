package com.example.hl7project.service;

import com.example.hl7project.configuration.TwilioConfig;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.PatientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ADTService {

    @Autowired
    private TwillioService twillioService;

    @Autowired
    private TwilioConfig twilioConfig;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PatientRepository patientRepository;

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

    private Map<String, String> extractPatientDataFromPidSegment(List<String> pidSegment) {
        Map<String, String> patientData = new HashMap<>();

        // Check if pidSegment is null or empty
        if (pidSegment == null || pidSegment.isEmpty()) {
            System.out.println("PID segment is null or empty.");
            return patientData; // Return an empty map or handle it as needed
        }

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

        return patientData;
    }

    private Map<String, String> extractDataFromMshSegment(List<String> mshSegment) {
        Map<String, String> mshData = new HashMap<>();

        // Accessing relevant indices based on the MSH segment structure
        mshData.put("Segment Type ID", (mshSegment.size() > 0) ? mshSegment.get(0) : null); // MSH.00
        mshData.put("Encoding Characters", (mshSegment.size() > 1) ? mshSegment.get(1) : null); // MSH.01
        mshData.put("Sending Application", (mshSegment.size() > 2) ? mshSegment.get(2) : null); // MSH.02
        mshData.put("Sending Facility", (mshSegment.size() > 3) ? mshSegment.get(3) : null); // MSH.03
        mshData.put("Receiving Application", (mshSegment.size() > 4) ? mshSegment.get(4) : null); // MSH.04
        mshData.put("Receiving Facility", (mshSegment.size() > 5) ? mshSegment.get(5) : null); // MSH.05
        mshData.put("Message Date and Time", (mshSegment.size() > 6) ? mshSegment.get(6) : null); // MSH.06
        mshData.put("Message Type", (mshSegment.size() > 8) ? mshSegment.get(8) : null); // MSH.06
        mshData.put("Message Control ID", (mshSegment.size() > 9) ? mshSegment.get(9) : null); // MSH.09
        mshData.put("Processing ID", (mshSegment.size() > 10) ? mshSegment.get(10) : null); // MSH.10
        mshData.put("Version ID", (mshSegment.size() > 11) ? mshSegment.get(11) : null); // MSH.11

        return mshData;
    }

    private Map<String, String> extractDataFromEvnSegment(List<String> evnSegment) {
        Map<String, String> evnData = new HashMap<>();

        // Accessing relevant indices based on the EVN segment structure
        evnData.put("Segment Type ID", (evnSegment.size() > 0) ? evnSegment.get(0) : null); // EVN.00
        evnData.put("Event Type Code", (evnSegment.size() > 1) ? evnSegment.get(1) : null); // EVN.01
        evnData.put("Recorded Date/Time", (evnSegment.size() > 2) ? evnSegment.get(2) : null); // EVN.02

        return evnData;
    }

    private void savePatientData(Map<String, String> patientData) {
        Patient patient = new Patient();
        patient.setId(patientData.get("Patient ID"));
        patient.setExternalPatientId(patientData.get("External Patient ID"));
        patient.setName(patientData.get("Patient Name"));
        patient.setDateOfBirth(patientData.get("Date of Birth"));
        patient.setSex(patientData.get("Sex"));
        patient.setRace(patientData.get("Race"));
        patient.setAddress(patientData.get("Patient Address"));
        patient.setPhoneNumber(patientData.get("Home Phone Number"));
        patient.setAdditionalPhone(patientData.get("Additional Phone"));
        patient.setLanguage(patientData.get("Primary Language"));
        patient.setMaritalStatus(patientData.get("Marital Status"));
        patient.setAccountNumber(patientData.get("Patient Account Number"));
        patient.setSsn(patientData.get("SSN"));
//        patient.setEthnicity(patientData.get("Ethnicity"));
//        patient.setDefaultLocation(patientData.get("Default Location"));

        patientRepository.save(patient);
        System.out.println("Patient data saved: " + patientData);
    }

    private String getMessageTemplate(String messageType, String patientId, String patientName) {
        switch (messageType) {
            case "A01":
                return String.format("Dear %s (ID: %s), you have been admitted. Welcome!", patientName, patientId);
            case "A02":
                return String.format("Dear %s (ID: %s), your transfer has been processed successfully.", patientName, patientId);
            case "A03":
                return String.format("Dear %s (ID: %s), you have been discharged. Thank you for visiting us!", patientName, patientId);
            case "A04":
                return String.format("Dear %s (ID: %s), your appointment has been scheduled.", patientName, patientId);
            case "A28":
                return String.format("Dear %s (ID: %s), your patient update has been noted.", patientName, patientId);
            case "A31":
                return String.format("Dear %s (ID: %s), your patient transfer is now complete.", patientName, patientId);
            case "A34":
                return String.format("Dear %s (ID: %s), your patient information has been updated successfully.", patientName, patientId);
            default:
                return "Dear patient, we have received your message.";
        }
    }

    public Message processMessage(String hl7Message) {
        Map<String, List<String>> hl7Map = receiveHl7Message(hl7Message);

        // Extract segments
        Map<String, String> mshData = extractDataFromMshSegment(hl7Map.get("MSH"));
        Map<String, String> patientData = extractPatientDataFromPidSegment(hl7Map.get("PID"));

        // Get patient information
        String patientId = patientData.get("Patient ID");
        String patientName = patientData.get("Patient Name");

        // Identify the message type
        String messageType = mshData.get("Message Type");

        // Determine action based on message type
        switch (messageType) {
            case "ADT^A01":
            case "ADT^A02":
            case "ADT^A03":
            case "ADT^A04":
            case "ADT^A28":
            case "ADT^A31":
            case "ADT^A34":
                // Save patient data
                savePatientData(patientData);

                // Generate and send the SMS
                String smsMessage = getMessageTemplate(messageType, patientId, patientName);
                String recipientPhoneNumber = patientData.get("Home Phone Number"); // Ensure this is the correct format
                //twillioService.getTwilioService(smsMessage, recipientPhoneNumber);
                break;
            default:
                System.out.println("Unknown message type: " + messageType);
                break;
        }

        return null; // Replace with actual return logic
    }


    // Parse the HL7 message
//        List<List<String>> parsedMessage = parseHl7Message(hl7Message);
//
//        // Extract necessary information
//        String patientId = extractPatientId(parsedMessage);
//        String patientName = extractPatientName(parsedMessage);
//        String patientAddress = extractPatientAddress(parsedMessage);
//        String patientPhone = extractPhoneNumber(parsedMessage);
//
//
//        System.out.println("patientId>>>>" + patientId);
//        System.out.println("patientName>>>" + patientName);
//        System.out.println("patientAddress>>>>" + patientAddress);
//        System.out.println("patientPhone>>>>" + patientPhone);
//        // Create or update Patient entity
//
//        // Prepare the Twilio message
//        String messageBody = String.format(" Your patient is registered with with details Patient Name: %s, Patient ID: %s, Address: %s, Phone: %s",
//                patientName, patientId, patientAddress, patientPhone);
//
//        try {
//            if (!patientRepository.existsByPatientId(patientId)) {
//                //Message message = twillioService.getTwilioService(messageBody, "919521052782");
//                savePatientInfo(patientId, patientName, patientPhone, patientAddress);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>("Error processing message", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return new ResponseEntity<>("Message processed successfully", HttpStatus.OK);
//    }

    private List<List<String>> parseHl7Message(String hl7Message) {
        List<List<String>> parsedMessage = new ArrayList<>();
        String[] lines = hl7Message.split("\r");
        for (String line : lines) {
            String[] fields = line.split("\\|");
            parsedMessage.add(Arrays.asList(fields));
        }
        return parsedMessage;
    }

//    public void savePatientInfo(String patientId, String patientName, String patientPhone, String patientAddress) {
//
////        Patient patient = patientRepository.findByPatientId(patientId);
////        if (patient == null) {
//        Patient patient = new Patient();
//        patient.setPatientId(patientId);
//        patient.setPatientName(patientName);
//        patient.setPatientAddress(patientAddress);
//        patient.setPatientPhone(patientPhone);
//        patientRepository.save(patient);
//    }

    private String extractPatientName(List<List<String>> parsedMessage) {
        String fullName = extractField(parsedMessage, 2, 5);
        String[] nameParts = fullName.split("\\^");
        if (nameParts.length >= 2) {
            return nameParts[1] + " " + nameParts[0];
        }
        return fullName;
    }

    private String extractPatientAddress(List<List<String>> parsedMessage) {
        String address = extractField(parsedMessage, 2, 11);
//        String[] nameParts = fullName.split("\\^");
//        if (nameParts.length >= 2) {
//            return nameParts[1] + " " + nameParts[0];
//        }
        return address;
    }

    private String extractPatientId(List<List<String>> parsedMessage) {
        String patientId = extractField(parsedMessage, 2, 1);
//        String[] nameParts = fullName.split("\\^");
//        if (nameParts.length >= 2) {
//            return nameParts[1] + " " + nameParts[0];
//        }
        return patientId;
    }

    private String extractPhoneNumber(List<List<String>> parsedMessage) {
        String countryCode = extractField(parsedMessage, 2, 12);
        String phNumber = extractField(parsedMessage, 2, 13);
        return countryCode + phNumber;
    }

    private String extractField(List<List<String>> parsedMessage, int segmentIndex, int fieldIndex) {
        if (parsedMessage.size() > segmentIndex && parsedMessage.get(segmentIndex).size() > fieldIndex) {
            return parsedMessage.get(segmentIndex).get(fieldIndex);
        }
        return "";

    }
}