package com.example.hl7project.service;

import com.example.hl7project.dto.AppointmentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class HL7UtilityService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AppointmentRequest appointmentRequest=new AppointmentRequest();

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

//    public String convertJsonToHl7(String messageType, Map<String, Object> patientData) {
//        // Use the patientData map to build the HL7 message.
//        StringBuilder hl7Message = new StringBuilder();
//
//        // Build the MSH segment
//        hl7Message.append(builSegments(messageType));
//
//        // Build the PID segment with patient data
//       // hl7Message.append(buildPIDSegment(patientData));
//
//        System.out.println("hl7Message:::" + hl7Message);
//        // Return the constructed HL7 message
//        return hl7Message.toString();
//    }

    // Method to build the MSH segment
//    public String convertJsonToADTHL7(AppointmentRequest jsonString) {
//        try {
//            // Parse JSON
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(String.valueOf(appointmentRequest));
//
//            // Build MSH Segment
//            StringBuilder hl7Message = new StringBuilder();
//            System.out.println("hl7Message}}}" + hl7Message);
//            hl7Message.append("MSH|^~\\&|")
//                    .append(rootNode.path("sendingApplication").asText("ECW")).append("|")
//                    .append(rootNode.path("sendingFacility").asText("ECW")).append("|")
//                    .append(rootNode.path("receivingApplication").asText("ECW")).append("|")
//                    .append(rootNode.path("receivingFacility").asText("ECW")).append("|")
//                    .append(rootNode.path("dateTimeOfMessage").asText(String.valueOf(LocalDateTime.now()))).append("||")
//                    .append(rootNode.path("messageType").asText("ADT^A28")).append("|")
//                    .append(rootNode.path("messageControlId").asText("")).append("|")
//                    .append(rootNode.path("processingId").asText("P")).append("|")
//                    .append(rootNode.path("versionId").asText("2.4")).append("\n");
//
//            hl7Message.append("EVN|")
//                    .append(rootNode.path("eventTypeCode").asText("A28")).append("|")
//                    .append(rootNode.path("recordedDateTime").asText(String.valueOf(LocalDateTime.now()))).append("|")
//                    .append(rootNode.path("plannedEventDateTime").asText("")).append("|\n");
//
//            // Build PID Segment
//            JsonNode patient = rootNode.path("patient");
//            hl7Message.append("PID|||")
//                    .append(patient.path("id").asText(appointmentRequest.getPatient().getMrnNo())).append("||")
//                    .append(patient.path("lastName").asText(appointmentRequest.getPatient().getFirstName())).append("^")
//                    .append(patient.path("firstName").asText(appointmentRequest.getPatient().getLastName())).append("^")
//                    .append(patient.path("middleName").asText(appointmentRequest.getPatient().getMiddleName())).append("||")
//                    .append(patient.path("dob").asText(appointmentRequest.getPatient().getDob())).append("|")
//                    .append(patient.path("sex").asText(appointmentRequest.getPatient().getSex())).append("|||")
//                    .append(patient.path("address").path("street").asText(appointmentRequest.getPatient().getAddress().getStreet())).append("^")
//                    .append(patient.path("address").path("city").asText(appointmentRequest.getPatient().getAddress().getCity())).append("^")
//                    .append(patient.path("address").path("state").asText(appointmentRequest.getPatient().getAddress().getState())).append("^")
//                    .append(patient.path("address").path("zip").asText(appointmentRequest.getPatient().getAddress().getZip())).append("||")
//                    .append(patient.path("phone").asText(appointmentRequest.getPatient().getPhone())).append("\n");
//
//
//            // Build PV1 Segment
//            JsonNode visit = rootNode.path("visit");
//            hl7Message.append("PV1||")
//                    .append(visit.path("patientClass").asText(appointmentRequest.getVisit().getPatientClass())).append("|")
//                    .append(visit.path("assignedLocation").path("pointOfCare").asText(appointmentRequest.getVisit().getAssignedLocation().getPointOfCare())).append("^")
//                    .append(visit.path("assignedLocation").path("room").asText(appointmentRequest.getVisit().getAssignedLocation().getRoom())).append("^")
//                    .append(visit.path("assignedLocation").path("bed").asText(appointmentRequest.getVisit().getAssignedLocation().getBed())).append("|||")
//                    .append(visit.path("attendingDoctor").path("id").asText(appointmentRequest.getVisit().getAttendingDoctor().getId())).append("^")
//                    .append(visit.path("attendingDoctor").path("lastName").asText(appointmentRequest.getVisit().getAttendingDoctor().getLastName())).append("^")
//                    .append(visit.path("attendingDoctor").path("firstName").asText(appointmentRequest.getVisit().getAttendingDoctor().getFirstName())).append("|")
//                    .append("||||||||||||")
//                    .append(visit.path("visitNumber").asText(appointmentRequest.getVisit().getVisitNumber())).append("|")
//                    .append("|||||||||||||||||")
//                    .append(visit.path("admitDate").asText(appointmentRequest.getVisit().getAdmitDate())).append("\n");
//
//            JsonNode insurance = rootNode.path("insurance");
//            if (!insurance.isMissingNode()) {
//                hl7Message.append("IN1|1|")
//                        .append(insurance.path("planId").asText("")).append("|")
//                        .append(insurance.path("companyName").asText("")).append("|")
//                        .append(insurance.path("address").path("street").asText("")).append("^")
//                        .append(insurance.path("address").path("city").asText("")).append("^")
//                        .append(insurance.path("address").path("state").asText("")).append("^")
//                        .append(insurance.path("address").path("zip").asText("")).append("|||")
//                        .append(insurance.path("policyNumber").asText("")).append("\n");
//            }
//
//            return hl7Message.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }
    public String convertJsonToADTHL7(AppointmentRequest appointmentRequest) {
        try {
            // Build MSH Segment
            StringBuilder hl7Message = new StringBuilder();
            hl7Message.append("MSH|^~\\&|")
                    .append(appointmentRequest.getSendingApplication() != null ? appointmentRequest.getSendingApplication() : "ECW").append("|")
                    .append(appointmentRequest.getSendingFacility() != null ? appointmentRequest.getSendingFacility() : "ECW").append("|")
                    .append(appointmentRequest.getReceivingApplication() != null ? appointmentRequest.getReceivingApplication() : "ECW").append("|")
                    .append(appointmentRequest.getReceivingFacility() != null ? appointmentRequest.getReceivingFacility() : "ECW").append("|")
                    .append(appointmentRequest.getDateTimeOfMessage() != null ? appointmentRequest.getDateTimeOfMessage() : LocalDateTime.now()).append("||")
                    .append(appointmentRequest.getMessageType() != null ? appointmentRequest.getMessageType() : "ADT^A28").append("|")
                    .append(appointmentRequest.getMessageControlId() != null ? appointmentRequest.getMessageControlId() : "").append("|")
                    .append(appointmentRequest.getProcessingId() != null ? appointmentRequest.getProcessingId() : "P").append("|")
                    .append(appointmentRequest.getVersionId() != null ? appointmentRequest.getVersionId() : "2.4").append("\n");

            // Build EVN Segment
//            hl7Message.append("EVN|")
//                    .append(appointmentRequest.getEventTypeCode() != null ? appointmentRequest.getEventTypeCode() : "A28").append("|")
//                    .append(appointmentRequest.getRecordedDateTime() != null ? appointmentRequest.getRecordedDateTime() : LocalDateTime.now()).append("|")
//                    .append(appointmentRequest.getPlannedEventDateTime() != null ? appointmentRequest.getPlannedEventDateTime() : "").append("\n");

            // Build PID Segment
            AppointmentRequest.Patient patient = appointmentRequest.getPatient();
            if (patient != null) {
                hl7Message.append("PID|||")
                        .append(patient.getMrnNo() != null ? patient.getMrnNo() : "").append("||")
                        .append(patient.getLastName() != null ? patient.getLastName() : "").append("^")
                        .append(patient.getFirstName() != null ? patient.getFirstName() : "").append("^")
                        .append(patient.getMiddleName() != null ? patient.getMiddleName() : "").append("||")
                        .append(patient.getDob() != null ? patient.getDob() : "").append("|")
                        .append(patient.getSex() != null ? patient.getSex() : "").append("|||");

                AppointmentRequest.Address address = patient.getAddress();
                if (address != null) {
                    hl7Message.append(address.getStreet() != null ? address.getStreet() : "").append("^")
                            .append(address.getCity() != null ? address.getCity() : "").append("^")
                            .append(address.getState() != null ? address.getState() : "").append("^")
                            .append(address.getZip() != null ? address.getZip() : "").append("|");
                }
                hl7Message.append(patient.getPhone() != null ? patient.getPhone() : "").append("\n");
            }

            // Build PV1 Segment
            AppointmentRequest.Visit visit = appointmentRequest.getVisit();
            if (visit != null) {
                hl7Message.append("PV1||")
                        .append(visit.getPatientClass() != null ? visit.getPatientClass() : "").append("|");

                AppointmentRequest.AssignedLocation location = visit.getAssignedLocation();
                if (location != null) {
                    hl7Message.append(location.getPointOfCare() != null ? location.getPointOfCare() : "").append("^")
                            .append(location.getRoom() != null ? location.getRoom() : "").append("^")
                            .append(location.getBed() != null ? location.getBed() : "").append("|");
                }

                AppointmentRequest.Doctor doctor = visit.getAttendingDoctor();
                if (doctor != null) {
                    hl7Message.append(doctor.getId() != null ? doctor.getId() : "").append("^")
                            .append(doctor.getLastName() != null ? doctor.getLastName() : "").append("^")
                            .append(doctor.getFirstName() != null ? doctor.getFirstName() : "").append("|");
                }
                hl7Message.append("||||||||||||")
                        .append(visit.getVisitNumber() != null ? visit.getVisitNumber() : "").append("|")
                        .append("|||||||||||||||||")
                        .append(visit.getAdmitDate() != null ? visit.getAdmitDate() : "").append("\n");
            }

            // Build IN1 Segment
            AppointmentRequest.Insurance insurance = appointmentRequest.getInsurance();
            if (insurance != null) {
                hl7Message.append("IN1|1|")
                        .append(insurance.getPlanId() != null ? insurance.getPlanId() : "").append("|")
                        .append(insurance.getCompanyName() != null ? insurance.getCompanyName() : "").append("|");

                AppointmentRequest.Address insuranceAddress = insurance.getAddress();
                if (insuranceAddress != null) {
                    hl7Message.append(insuranceAddress.getStreet() != null ? insuranceAddress.getStreet() : "").append("^")
                            .append(insuranceAddress.getCity() != null ? insuranceAddress.getCity() : "").append("^")
                            .append(insuranceAddress.getState() != null ? insuranceAddress.getState() : "").append("^")
                            .append(insuranceAddress.getZip() != null ? insuranceAddress.getZip() : "");
                }
                hl7Message.append("|||")
                        .append(insurance.getPolicyNumber() != null ? insurance.getPolicyNumber() : "").append("\n");
            }

            return hl7Message.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> extractPatientDetailsFromJson(AppointmentRequest json) {
        Map<String, String> patientDetails = new HashMap<>();
        JsonObject jsonObject = new JsonObject();

        // Extract the patient details using the keys from the JSON structure
        // Adjust the key names based on your actual JSON structure
        patientDetails.put("firstName", json.getPatient().getFirstName());
        patientDetails.put("lastName", json.getPatient().getLastName());
        patientDetails.put("dob", json.getPatient().getDob()); // Make sure this matches the JSON key
        System.out.println("Extracted DOB: " + json.getPatient().getDob());

        return patientDetails;
    }
    public StringBuilder buildSIUHl7Message(AppointmentRequest appointmentRequest) {
        try {
            // Build MSH Segment
            StringBuilder hl7Message = new StringBuilder();
            hl7Message.append("MSH|^~\\&|")
                    .append(appointmentRequest.getSendingApplication() != null ? appointmentRequest.getSendingApplication() : "ECW").append("|")
                    .append(appointmentRequest.getSendingFacility() != null ? appointmentRequest.getSendingFacility() : "ECW").append("|")
                    .append(appointmentRequest.getReceivingApplication() != null ? appointmentRequest.getReceivingApplication() : "ECW").append("|")
                    .append(appointmentRequest.getReceivingFacility() != null ? appointmentRequest.getReceivingFacility() : "ECW").append("|")
                    .append(appointmentRequest.getDateTimeOfMessage() != null ? appointmentRequest.getDateTimeOfMessage() : LocalDateTime.now()).append("||")
                    .append(appointmentRequest.getMessageType() != null ? appointmentRequest.getMessageType() : "SIU^S12").append("|")
                    .append(appointmentRequest.getMessageControlId() != null ? appointmentRequest.getMessageControlId() : "").append("|")
                    .append(appointmentRequest.getProcessingId() != null ? appointmentRequest.getProcessingId() : "P").append("|")
                    .append(appointmentRequest.getVersionId() != null ? appointmentRequest.getVersionId() : "2.4").append("\n");

            // Build SCH Segment (Schedule)
            hl7Message.append("SCH|")
                    .append(appointmentRequest.getScheduleId() != null ? appointmentRequest.getScheduleId() : "").append("|")
                    .append(appointmentRequest.getEventReasonCode() != null ? appointmentRequest.getEventReasonCode() : "").append("|")
                    .append(appointmentRequest.getAppointmentType() != null ? appointmentRequest.getAppointmentType() : "").append("|")
                    .append(appointmentRequest.getDateTimeOfTheEvent() != null ? appointmentRequest.getDateTimeOfTheEvent() : LocalDateTime.now()).append("|")
                    .append(appointmentRequest.getDuration() != null ? appointmentRequest.getDuration() : "").append("|")
                    .append(appointmentRequest.getDurationUnits() != null ? appointmentRequest.getDurationUnits() : "").append("\n");

//            AppointmentRequest appointment = new AppointmentRequest();
            if (appointmentRequest != null) {
                hl7Message.append("AIG|")
                        .append(appointmentRequest.getId() != null ? appointmentRequest.getId() : "").append("|")
                        .append(appointmentRequest.getResourceId() != null ? appointmentRequest.getResourceId() : "").append("|")
                        .append(appointmentRequest.getStartDateTime() != null ? appointmentRequest.getStartDateTime() : "").append("|||")
                        .append(appointmentRequest.getDuration() != null ? appointmentRequest.getDuration() : "").append("|")
                        .append(appointmentRequest.getDurationUnits() != null ? appointmentRequest.getDurationUnits() : "").append("\n");
            }

            // Build AIL Segment (Location Information)
            AppointmentRequest.Location location = appointmentRequest.getLocation();
            if (location != null) {
                hl7Message.append("AIL|")
                        .append(location.getLocationId() != null ? location.getLocationId() : "").append("|")
//                        .append(location.getLocationName() != null ? location.getLocationResourceId() : "").append("|")
                        .append(location.getLocationName() != null ? location.getLocationName() : "").append("\n");
            }

            // Build AIP Segment (Ordering Provider Information)
            AppointmentRequest.Provider provider = appointmentRequest.getProvider();
            if (provider != null) {
                hl7Message.append("AIP|")
                        .append(provider.getProviderId() != null ? provider.getProviderId() : "").append("|")
                        .append(provider.getDuration() != null ? provider.getDuration() : "").append("|")
                        .append(provider.getLastName() != null ? provider.getLastName() : "").append("^")
                        .append(provider.getFirstName() != null ? provider.getFirstName() : "").append("\n");
            }
            return hl7Message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//        StringBuilder hl7Message = new StringBuilder();
//
//        try {
//
//            // Parse JSON
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(jsonString);
//
//            // Build MSH Segment
//            hl7Message.append("MSH|^~\\&|")
//                    .append(rootNode.path("sendingApplication").asText("DefaultApp")).append("|")
//                    .append(rootNode.path("sendingFacility").asText("DefaultFacility")).append("|")
//                    .append(rootNode.path("receivingApplication").asText("DefaultDestApp")).append("|")
//                    .append(rootNode.path("receivingFacility").asText("DefaultDestFacility")).append("|")
//                    .append(rootNode.path("dateTimeOfMessage").asText("20241128120000")).append("||")
//                    .append(rootNode.path("messageType").asText("SIU^S12")).append("|")
//                    .append(rootNode.path("messageControlId").asText("MSG12345")).append("|")
//                    .append(rootNode.path("processingId").asText("P")).append("|")
//                    .append(rootNode.path("versionId").asText("2.4")).append("\n");
//
//            // Build SCH Segment
//            JsonNode schedule = rootNode.path("schedule");
//            hl7Message.append("SCH|")
//                    .append(schedule.path("visitAppointmentId").asText("VISIT12345")).append("|")
//                    .append(schedule.path("vendorVisitNumber").asText("VENDOR12345")).append("|")
//                    .append(schedule.path("ecwVisitNumber").asText("ECW12345")).append("|")
//                    .append(schedule.path("eventReason").asText("")).append("|")
//                    .append(schedule.path("appointmentReason").asText("Reason for appointment")).append("|")
//                    .append(schedule.path("appointmentVisitTypeCode").asText("AVT123")).append("^")
//                    .append(schedule.path("appointmentVisitType").asText("Type of appointment")).append("|")
//                    .append(schedule.path("appointmentDuration").asText("30")).append("|")
//                    .append(schedule.path("appointmentDurationUnits").asText("m")).append("|")
//                    .append(schedule.path("appointmentTimingQuantity").asText("30^20241128120000^20241128123000")).append("|")
//                    .append(schedule.path("resourceName").asText("Resource123")).append("|")
//                    .append(schedule.path("encounterNotes").asText("General notes")).append("|")
//                    .append(schedule.path("visitStatusCode").asText("Arrived")).append("\n");
//
//            // Build PID Segment
//            JsonNode patient = rootNode.path("patient");
//            hl7Message.append("PID|||")
//                    .append(patient.path("id").asText("12345")).append("||")
//                    .append(patient.path("lastName").asText("Doe")).append("^")
//                    .append(patient.path("firstName").asText("John")).append("^")
//                    .append(patient.path("middleName").asText("M")).append("||")
//                    .append(patient.path("dob").asText("19800101")).append("|")
//                    .append(patient.path("sex").asText("M")).append("|||")
//                    .append(patient.path("address").path("street").asText("")).append("^")
//                    .append(patient.path("address").path("city").asText("")).append("^")
//                    .append(patient.path("address").path("state").asText("")).append("^")
//                    .append(patient.path("address").path("zip").asText("")).append("||")
//                    .append(patient.path("phone").asText("1234567890")).append("\n");
//
//            // Build PV1 Segment
//            JsonNode visit = rootNode.path("visit");
//            hl7Message.append("PV1||")
//                    .append(visit.path("patientClass").asText("O")).append("|")
//                    .append(visit.path("assignedLocation").path("pointOfCare").asText("")).append("^")
//                    .append(visit.path("assignedLocation").path("room").asText("")).append("^")
//                    .append(visit.path("assignedLocation").path("bed").asText("")).append("|||")
//                    .append(visit.path("attendingDoctor").path("id").asText("")).append("^")
//                    .append(visit.path("attendingDoctor").path("lastName").asText("")).append("^")
//                    .append(visit.path("attendingDoctor").path("firstName").asText("")).append("|")
//                    .append("||||||||||||")
//                    .append(visit.path("visitNumber").asText("VN12345")).append("|")
//                    .append("|||||||||||||||||")
//                    .append(visit.path("admitDate").asText("20241128")).append("\n");
//
//            JsonNode resource = rootNode.path("resource");
//            hl7Message.append("AIG|1||")
//                    .append(resource.path("resourceId").asText("1234")).append("^")
//                    .append(resource.path("lastName").asText("Test")).append("^")
//                    .append(resource.path("firstName").asText("Test")).append("|||||")
//                    .append(resource.path("startDateTime").asText("20020108150000")).append("|||")
//                    .append(resource.path("duration").asText("10")).append("|")
//                    .append(resource.path("durationUnits").asText("m")).append("\n");
//
//            // Build AIL Segment
//            JsonNode location = rootNode.path("location");
//            hl7Message.append("AIL|1||")
//                    .append(location.path("locationId").asText("1234")).append("^")
//                    .append(location.path("locationName").asText("TesteCWFacility")).append("\n");
//
//            // Build AIP Segment
//            JsonNode provider = rootNode.path("provider");
//            hl7Message.append("AIP|1||")
//                    .append(provider.path("providerId").asText("5678")).append("^")
//                    .append(provider.path("lastName").asText("Smith")).append("^")
//                    .append(provider.path("firstName").asText("John")).append("|||||")
//                    .append(provider.path("startDateTime").asText("20241128120000")).append("|||")
//                    .append(provider.path("duration").asText("15")).append("|")
//                    .append(provider.path("durationUnits").asText("m")).append("||")
//                    .append(provider.path("statusCode").asText("Arrived")).append("\n");
//
//        } catch (JsonMappingException e) {
//            throw new RuntimeException(e);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("siu hl7 message:::"+hl7Message);
//        return hl7Message;
//    }

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
