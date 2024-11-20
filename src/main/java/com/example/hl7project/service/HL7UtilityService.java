package com.example.hl7project.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HL7UtilityService {

    public Map<String, List<String>> parseHl7Message(String hl7Message) {
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

    private String generateExternalMRN(String vendorPrefix) {
        // Use UUID for uniqueness or implement your logic here for unique generation
        String uniqueID = UUID.randomUUID().toString().replace("-", "").toUpperCase();  // Example
        return vendorPrefix + "-" + uniqueID;
    }


    public Map<String, String> extractPatientData(List<String> pidSegment) {

        Map<String, String> patientData = new HashMap<>();

        // Generate a unique External MRN (Vendor MRN)
        String vendorPrefix = "VENDOR123";  // Customize this as needed
        String externalMRN = generateExternalMRN(vendorPrefix);
        if (pidSegment.size() > 3 && pidSegment.get(3) == null) {
            pidSegment.set(3, externalMRN);
        } else if (pidSegment.size() > 3) {
            patientData.put("External Patient MRN", pidSegment.get(3));
        }
        patientData.put("External Patient MRN", externalMRN);
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

    public Map<String, String> extractDataFromSchSegment(List<String> schSegment) {
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
    public Map<String, String> extractDataFromMshSegment(List<String> mshSegment) {
        Map<String, String> mshData = new HashMap<>();
        System.out.println("SCH Segment: " + mshSegment);
        // Accessing relevant indices based on the SCH segment structure
        mshData.put("Segment Type ID", (mshSegment.size() > 0) ? mshSegment.get(0) : null); // SCH.00 - Required
        mshData.put("messageType", (mshSegment.size() > 8) ? mshSegment.get(8) : null); // SCH.08 - Required
        mshData.put("messageDateTime", (mshSegment.size() > 6) ? mshSegment.get(6) : null); // SCH.08 - Required

        System.out.println("mshData::: " + mshData);
        return mshData;
    }
}
