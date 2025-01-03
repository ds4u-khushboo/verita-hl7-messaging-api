package com.example.hl7project.service;

import com.example.hl7project.dto.AppointmentRequest;
import com.example.hl7project.dto.BookingInfoDTO;
import com.example.hl7project.model.Provider;
import com.example.hl7project.model.Resource;
import com.example.hl7project.repository.ProviderRepository;
import com.example.hl7project.repository.ResourceRepository;
import com.example.hl7project.utility.Utility;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class HL7UtilityService {

    @Autowired
    private Utility utilities;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ProviderRepository providerRepository;

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

    private String generateOutboundExternalMRN() {
        SecureRandom random = new SecureRandom();
        long uniqueId = random.nextLong();

        // Ensure that the ID is positive and numeric
        uniqueId = Math.abs(uniqueId);

        // Optionally, ensure the number is of a certain length (for example, 16 digits)
        String numericID = String.format("%016d", uniqueId);

        System.out.println("vendorMRN: " + numericID);
        return numericID;
    }

    public String buildADTHL7Message(AppointmentRequest appointmentRequest) {
        try {
            // Build MSH Segment
            StringBuilder hl7Message = new StringBuilder();
            hl7Message.append("\u000B");
            hl7Message.append("MSH|^~\\&|")
                    .append(appointmentRequest.getSendingApplication() != null ? appointmentRequest.getSendingApplication() : "ECW").append("|")
                    .append(appointmentRequest.getSendingFacility() != null ? appointmentRequest.getSendingFacility() : "ECW").append("|")
                    .append(appointmentRequest.getReceivingApplication() != null ? appointmentRequest.getReceivingApplication() : "ECW").append("|")
                    .append(appointmentRequest.getReceivingFacility() != null ? appointmentRequest.getReceivingFacility() : 303492).append("|")
                    .append(appointmentRequest.getDateTimeOfMessage() != null ? appointmentRequest.getDateTimeOfMessage() : utilities.formatToHL7DateTime(LocalDateTime.now())).append("||")
                    .append(appointmentRequest.getMessageType() != null ? appointmentRequest.getMessageType() : "ADT^A28").append("|")
                    .append(appointmentRequest.getMessageControlId() != null ? appointmentRequest.getMessageControlId() : "").append("|")
                    .append(appointmentRequest.getProcessingId() != null ? appointmentRequest.getProcessingId() : "T").append("|")
                    .append(appointmentRequest.getVersionId() != null ? appointmentRequest.getVersionId() : "2.4").append("\n");

            hl7Message.append("EVN|")
                    .append("ADT^A28").append("|")
                    .append(utilities.formatToHL7DateTime(LocalDateTime.now())).append("\n");

            AppointmentRequest.Patient patient = appointmentRequest.getPatient();
            if (patient != null) {
                hl7Message.append("PID|1|")
                        .append("").append("|") //ECW PatientId
                        .append(generateOutboundExternalMRN()).append("|")
                        .append("").append("|")  //ECW PatientId
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
                            .append(address.getZip() != null ? address.getZip() : "").append("||");
                }
                hl7Message.append(patient.getPhone() != null ? patient.getPhone() : "").
                        append(appointmentRequest.getPatient().getLanguage() != null ? patient.getLanguage() : "").append("|").
                        append(appointmentRequest.getPatient().getMaritalStatus() != null ? patient.getMaritalStatus() : "").append("|").
                        append(appointmentRequest.getPatient().getMaritalStatus() != null ? patient.getMaritalStatus() : "").append("|");
                hl7Message.append(patient.getStatementFlag() != null ? patient.getStatementFlag() : ""); // PID.24 - Statement Flag
                hl7Message.append("|");
                hl7Message.append(patient.getStatementDate() != null ? patient.getStatementDate() : ""); // PID.25 - Statement Signature Date
                hl7Message.append("|");

                // Patient's Death Date and Indicator (if applicable)
                hl7Message.append(patient.getDeathDate() != null ? patient.getDeathDate().replaceAll("-", "") : ""); // PID.29 - Patient Death Date
                hl7Message.append("|");
                hl7Message.append(patient.getDeathIndicator() != null ? patient.getDeathIndicator() : ""); // PID.30 - Patient Death Indicator

                // End of PID segment
                hl7Message.append("||||||||||||||||||||||||" + "\n");

            }
            AppointmentRequest.Visit visit = appointmentRequest.getVisit();
            if (visit != null) {
                hl7Message.append("PV1||")
                        .append(visit.getPatientClass() != null ? visit.getPatientClass() : "").append("|");

                AppointmentRequest.AssignedLocation location = visit.getAssignedLocation();
                if (location != null) {
                    hl7Message.append(location.getPointOfCare() != null ? location.getPointOfCare() : "").append("^")
                            .append(location.getRoom() != null ? location.getRoom() : "").append("^")
                            .append(location.getBed() != null ? location.getBed() : "").append("||||");
                }

                AppointmentRequest.Doctor doctor = visit.getAttendingDoctor();
                if (doctor != null) {
                    hl7Message.append(doctor.getId() != null ? doctor.getId() : "").append("^")
                            .append(doctor.getLastName() != null ? doctor.getLastName() : "").append("^")
                            .append(doctor.getFirstName() != null ? doctor.getFirstName() : "").append("|||").append("\n");
                }
//                hl7Message.append("||||||||||||")
//                        .append(visit.getVisitNumber() != null ? visit.getVisitNumber() : "").append("|")
//                        .append("|||||||||||||||||")
//                        .append(visit.getAdmitDate() != null ? formatHL7Date(visit.getAdmitDate()) : "").append("\n");
            }

            // Build IN1 Segment
//            AppointmentRequest.Insurance insurance = appointmentRequest.getInsurance();
//            if (insurance != null) {
//                hl7Message.append("IN1|1|")
//                        .append(insurance.getPlanId() != null ? insurance.getPlanId() : "").append("|")
//                        .append(insurance.getCompanyName() != null ? insurance.getCompanyName() : "").append("|");
//
//                AppointmentRequest.Address insuranceAddress = insurance.getAddress();
//                if (insuranceAddress != null) {
//                    hl7Message.append(insuranceAddress.getStreet() != null ? insuranceAddress.getStreet() : "").append("^")
//                            .append(insuranceAddress.getCity() != null ? insuranceAddress.getCity() : "").append("^")
//                            .append(insuranceAddress.getState() != null ? insuranceAddress.getState() : "").append("^")
//                            .append(insuranceAddress.getZip() != null ? insuranceAddress.getZip() : "");
//                }
//                hl7Message.append("|||")
//                        .append(insurance.getPolicyNumber() != null ? insurance.getPolicyNumber() : "").append("\n");
//            }
            // Build GT1 Segment (Guarantor Information)
            AppointmentRequest.Guarantor guarantor = appointmentRequest.getGuarantor();
            if (guarantor != null) {
                hl7Message.append("GT1|1|")
                        .append(guarantor.getGuarantorId() != null ? guarantor.getGuarantorId() : "").append("|")
                        .append(guarantor.getGuarantorName() != null ? guarantor.getGuarantorName() : "").append("|")
                        .append(guarantor.getGuarantorAddress() != null ? guarantor.getGuarantorAddress() : "").append("|")
                        .append(guarantor.getGuarantorPhone() != null ? guarantor.getGuarantorPhone() : "").append("|")
                        .append(guarantor.getGuarantorDob() != null ? guarantor.getGuarantorDob() : "").append("|")
                        .append(guarantor.getGuarantorSex() != null ? guarantor.getGuarantorSex() : "").append("|")
                        .append(guarantor.getGuarantorType() != null ? guarantor.getGuarantorType() : "").append("|")
                        .append(guarantor.getGuarantorRelationship() != null ? guarantor.getGuarantorRelationship() : "").append("|")
                        .append(guarantor.getGuarantorSSN() != null ? guarantor.getGuarantorSSN() : "").append("|")
                        .append(guarantor.getGuarantorEmploymentStatus() != null ? guarantor.getGuarantorEmploymentStatus() : "");

            }
            hl7Message.append("\u001C");
            return hl7Message.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatHL7Date(String hl7Date) throws Exception {
        // Parse the original HL7 date string (e.g., 20241129T121813.629972300)
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = originalFormat.parse(hl7Date);

        // Format the date to show 'yyyyMMddHHmmss' (date + hour + minute + second)
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return targetFormat.format(date);
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

    public String buildSIUHl7Message(AppointmentRequest appointmentRequest) {
        try {
            // Build MSH Segment
            StringBuilder hl7Message = new StringBuilder();
            System.out.println("LocalDateTime.now()" + LocalDateTime.now());
            hl7Message.append("\u000B");
            hl7Message.append("MSH|^~\\&|")
                    .append(appointmentRequest.getSendingApplication() != null ? appointmentRequest.getSendingApplication() : "ECW").append("|")
                    .append(appointmentRequest.getSendingFacility() != null ? appointmentRequest.getSendingFacility() : "ECW").append("|")
                    .append(appointmentRequest.getReceivingApplication() != null ? appointmentRequest.getReceivingApplication() : "ECW").append("|")
                    .append(appointmentRequest.getReceivingFacility() != null ? appointmentRequest.getReceivingFacility() : 303492).append("|")
                    .append(appointmentRequest.getDateTimeOfMessage() != null ? appointmentRequest.getDateTimeOfMessage() : utilities.formatToHL7DateTime(LocalDateTime.now())).append("||")
                    .append(appointmentRequest.getMessageType() != null ? appointmentRequest.getMessageType() : "SIU^S12").append("|")
                    .append(appointmentRequest.getMessageControlId() != null ? appointmentRequest.getMessageControlId() : "").append("|")
                    .append(appointmentRequest.getProcessingId() != null ? appointmentRequest.getProcessingId() : "T").append("|")
                    .append(appointmentRequest.getVersionId() != null ? appointmentRequest.getVersionId() : "2.4").append("\n");

            // Build SCH Segment (Schedule)
            hl7Message.append("SCH|")
                    .append(appointmentRequest.getVisitAppointmentIdECW() != null ? appointmentRequest.getVisitAppointmentIdECW() : "").append("|")
                    .append(appointmentRequest.getVisitAppointmentIdECW() != null ? appointmentRequest.getVisitAppointmentIdECW() : "").append("|||||")
                    .append(appointmentRequest.getAppointmentReason() != null ? appointmentRequest.getAppointmentReason() : "").append("|")
                    .append(appointmentRequest.getAppointmentVisitType() != null ? appointmentRequest.getAppointmentVisitType() : "").append("|||")
                    .append("^^^" + appointmentRequest.getStartDateTime() != null ? "^^^" + formatHL7Date(appointmentRequest.getStartDateTime()) : "").append("^")
                    .append(appointmentRequest.getEndDateTime() != null ? formatHL7Date(appointmentRequest.getEndDateTime()) : "").append("||||||||||||||").
//                     .append(appointmentRequest.getLocation() != null && appointmentRequest.getLocation().getLocationName() != null ? appointmentRequest.getLocation().getLocationName() : "").append("|") // Location
//                    .append(appointmentRequest.getProvider() != null && appointmentRequest.getProvider().getProviderId() != null ? appointmentRequest.getProvider().getProviderId() : "").append("|") // Provider ID
//                    .append(appointmentRequest.getResourceName() != null ? appointmentRequest.getResourceName() : "").append("|") // Resource Name
//                    .append(appointmentRequest.getEncounterNotes() != null ? appointmentRequest.getEncounterNotes() : "").append("|").
        append(appointmentRequest.getVisitStatusCode() != null ? "PEN" : "PEN").append("|||" + "\n");
            AppointmentRequest.Patient patient = appointmentRequest.getPatient();
            if (patient != null) {
                hl7Message.append("PID|1|")
                        .append("").append("|")  //ECW PatientId
                        .append(generateOutboundExternalMRN()).append("|")
                        .append("").append("|")   //ECW PatientId
                        .append(patient.getLastName() != null ? patient.getLastName() : "").append("^")
                        .append(patient.getFirstName() != null ? patient.getFirstName() : "").append("^")
                        .append(patient.getMiddleName() != null ? patient.getMiddleName() : "").append("||")
                        .append(patient.getDob() != null ? patient.getDob().replace("-", "") : "").append("|")
                        .append(patient.getSex() != null ? patient.getSex() : "").append("|||");

                AppointmentRequest.Address address = patient.getAddress();
                if (address != null) {
                    hl7Message.append(address.getStreet() != null ? address.getStreet() : "").append("^")
                            .append(address.getCity() != null ? address.getCity() : "").append("^")
                            .append(address.getState() != null ? address.getState() : "").append("^")
                            .append(address.getZip() != null ? address.getZip() : "").append("||");
                }
                hl7Message.append(patient.getPhone() != null ? patient.getPhone() : "").append("||").
                        append(appointmentRequest.getPatient().getLanguage() != null ? patient.getLanguage() : "").append("|").
                        append(appointmentRequest.getPatient().getMaritalStatus() != null ? patient.getMaritalStatus() : "").append("|||").
                        append(appointmentRequest.getPatient().getSsn() != null ? patient.getSsn() : "").append("||||||||||||||||||||" + "\n");
                hl7Message.append(patient.getStatementFlag() != null ? patient.getStatementFlag() : ""); // PID.24 - Statement Flag
//                hl7Message.append("|");
//                hl7Message.append(patient.getStatementDate() != null ? patient.getStatementDate() : ""); // PID.25 - Statement Signature Date
//                hl7Message.append("|");
//
//                // Patient's Death Date and Indicator (if applicable)
//                hl7Message.append(patient.getDeathDate() != null ? patient.getDeathDate().replaceAll("-", "") : ""); // PID.29 - Patient Death Date
//                hl7Message.append("|");
//                hl7Message.append(patient.getDeathIndicator() != null ? patient.getDeathIndicator() : ""); // PID.30 - Patient Death Indicator
//
//                // End of PID segment
//                hl7Message.append("||||||||||||||||||||||||" + "\n");

            }

            AppointmentRequest.Visit visit = appointmentRequest.getVisit();
            if (visit != null) {
                hl7Message.append("PV1|1|")
                        .append(visit.getPatientClass() != null ? visit.getPatientClass() : "").append("|");

                AppointmentRequest.AssignedLocation location = visit.getAssignedLocation();
                if (location != null) {
                    hl7Message.append(location.getPointOfCare() != null ? location.getPointOfCare() : "").append("^")
                            .append(location.getRoom() != null ? location.getRoom() : "").append("^")
                            .append(location.getBed() != null ? location.getBed() : "").append("||||");
                }

                AppointmentRequest.Doctor doctor = visit.getAttendingDoctor();
                if (doctor != null) {
                    hl7Message.append(doctor.getId() != null ? doctor.getId() : "").append("^")
                            .append(doctor.getLastName() != null ? doctor.getLastName() : "").append("^")
                            .append(doctor.getFirstName() != null ? doctor.getFirstName() : "").append("||||||||||||");

                }
                hl7Message.append(visit.getVisitNumber() != null ? visit.getVisitNumber() : "").append("|||||||||||||||||||||||||")
                        .append(appointmentRequest.getStartDateTime() != null ? formatHL7Date(String.valueOf(appointmentRequest.getStartDateTime())) : "")
                        .append("||||||||").append("\n");
            }
            hl7Message.append("AIG|||");
            if (appointmentRequest.getResource() != null) {
                Resource resource = resourceRepository.findByResourceType(appointmentRequest.getResource().getResourceType());
                if (resource == null) {
                    throw new Exception("Resource type does not exist in database");
                }

                hl7Message.append(resource.getResourceId() != null ? resource.getResourceId() : "").append("^")
                        .append(appointmentRequest.getResource().getResourceType() != null ? appointmentRequest.getResource().getResourceType() : "").append("^")
                        .append("").append("|").append("|||||");
            } else {
                AppointmentRequest.Provider provider = appointmentRequest.getProvider();
                String providerName = provider.getLastName() + "," + provider.getFirstName();
                Provider providerInDb = providerRepository.findByProviderName(providerName);
                if (providerInDb == null) {
                    throw new Exception("Provider does not exist in database");
                }
                String providerId = providerInDb.getProviderId() != null ? providerInDb.getProviderId() : "";
                String providerFirstName = provider.getFirstName() != null ? provider.getFirstName() : "";
                String providerLastName = provider.getLastName() != null ? provider.getLastName() : "";

                hl7Message.append(providerId).append("^")
                        .append(providerFirstName).append("^")
                        .append(providerLastName).append("^")
                        .append("").append("|");
            }


//             else {
//                // No resource required, use provider info
//                AppointmentRequest.Provider provider = appointmentRequest.getProvider();
//                if (provider != null) {
//                    hl7Message.append(provider.getProviderId() != null ? provider.getProviderId() : "").append("^")
//                            .append(provider.getProviderName() != null ? provider.getProviderName() : "").append("|");

            hl7Message.append(appointmentRequest.getStartDateTime() != null ? formatHL7Date(appointmentRequest.getStartDateTime()) : "").append("|||")
                    .append(appointmentRequest.getDuration() != null ? appointmentRequest.getDuration() : "").append("|")
                    .append(appointmentRequest.getDurationUnits() != null ? appointmentRequest.getDurationUnits() : "").append("\n");

            // AIL Segment (Location Information)
            AppointmentRequest.Location location = appointmentRequest.getLocation();
            if (location != null) {
                hl7Message.append("AIL|")
                        .append(location.getLocationId() != null ? location.getLocationId() : "").append("||")
                        .append(location.getLocationName() != null ? location.getLocationName() : "").append("|").append("\n");
            }

            // AIP Segment (Provider Information)
            AppointmentRequest.Provider provider = appointmentRequest.getProvider();
            if (provider != null) {
                hl7Message.append("AIP|1||");
                AppointmentRequest.Doctor doctor = visit.getAttendingDoctor();
                if (doctor != null) {
                    hl7Message.append(doctor.getId() != null ? doctor.getId() : "").append("^")
                            .append(doctor.getLastName() != null ? doctor.getLastName() : "").append("^")
                            .append(doctor.getFirstName() != null ? doctor.getFirstName() : "").append("||");
                }
            }

            hl7Message.append("\u001C");
            return hl7Message.toString();
        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }

    }


//    AppointmentRequest.Location location = appointmentRequest.getLocation();
//            if (location != null) {
//                hl7Message.append("AIL|")
//                        .append(location.getLocationId() != null ? location.getLocationId() : "").append("||")
////                        .append(location.getLocationName() != null ? location.getLocationResourceId() : "").append("|")
//                        .append(location.getLocationName() != null ? location.getLocationName() : "").append("|").append("\n");
//            }
//
//            AppointmentRequest.Provider provider = appointmentRequest.getProvider();
//            if (provider != null) {
//                hl7Message.append("AIP|1||");
//                AppointmentRequest.Doctor doctor = visit.getAttendingDoctor();
//                if (doctor != null) {
//                    hl7Message.append(doctor.getId() != null ? doctor.getId() : "").append("^")
//                            .append(doctor.getLastName() != null ? doctor.getLastName() : "").append("^")
//                            .append(doctor.getFirstName() != null ? doctor.getFirstName() : "").append("||");
//                }
//            }
//            hl7Message.append("\u001C");
//            return hl7Message.toString();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void buildAppointmentByHl7Message(AppointmentRequest appointmentRequest) {
        buildSIUHl7Message(appointmentRequest);
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

    private String generateExternalMRN(String vendorPrefix) {
        String uniqueID = UUID.randomUUID().toString().replace("-", "").toUpperCase();  // Example
        return vendorPrefix + "-" + uniqueID;
    }


    public Map<String, String> extractPatientData(List<String> pidSegment) {

        Map<String, String> patientData = new HashMap<>();
        String vendorPrefix = "VENDOR123";
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
        schData.put("Segment Type ID", (schSegment.size() > 0) ? schSegment.get(0) : null);
        schData.put("Temp Visit/Appointment ID", (schSegment.size() > 1) ? schSegment.get(1) : null);
        schData.put("Visit/Appointment ID", (schSegment.size() > 2) ? schSegment.get(2) : null);
        schData.put("Occurrence Number", (schSegment.size() > 3) ? schSegment.get(3) : null);
        schData.put("Placer Group Number", (schSegment.size() > 4) ? schSegment.get(4) : null);
        schData.put("Schedule ID", (schSegment.size() > 5) ? schSegment.get(5) : null);
        schData.put("Event Reason", (schSegment.size() > 6) ? schSegment.get(6) : null);
        schData.put("Appointment Reason", (schSegment.size() > 7) ? schSegment.get(7) : null);
        schData.put("Appointment Type", (schSegment.size() > 8) ? schSegment.get(8) : null);
        schData.put("Appointment Duration", (schSegment.size() > 9) ? schSegment.get(9) : null);
        schData.put("Appointment Duration Units", (schSegment.size() > 10) ? schSegment.get(10) : null);
        schData.put("Appointment Timing Quantity", (schSegment.size() > 11) ? schSegment.get(11) : null);
        String appointmentDate = (schSegment.size() > 11) ? schSegment.get(11) : null;
        if (appointmentDate != null) {
            String[] times = appointmentDate.split("\\^");
            if (times.length > 0) {
                String startTime = times[0];
                schData.put("Appointment Date", startTime.substring(0));
            }
        }

        schData.put("Resource Name", (schSegment.size() > 20) ? schSegment.get(20) : null);
        schData.put("Encounter Notes", (schSegment.size() > 24) ? schSegment.get(24) : null);
        schData.put("Visit Status Code", (schSegment.size() > 25) ? schSegment.get(25) : null);

        System.out.println("Extracted SCH Data:");
        for (Map.Entry<String, String> entry : schData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Extracted SCH Data: " + schData);
        return schData;
    }

    public Map<String, String> extractDataFromMshSegment(List<String> mshSegment) {
        Map<String, String> mshData = new HashMap<>();
        System.out.println("MSH Segment: " + mshSegment);
        mshData.put("Segment Type ID", (mshSegment.size() > 0) ? mshSegment.get(0) : null); // SCH.00 - Required
        mshData.put("messageType", (mshSegment.size() > 8) ? mshSegment.get(8) : null); // SCH.08 - Required
        mshData.put("messageDateTime", (mshSegment.size() > 6) ? mshSegment.get(6) : null); // SCH.08 - Required

        System.out.println("mshData::: " + mshData);
        return mshData;
    }

    public Map<String, String> extractDataFromPV1Segment(List<String> pv1Segment) {
        Map<String, String> pv1Data = new HashMap<>();
        System.out.println("PV1 Segment: " + pv1Segment);

        String providerField = (pv1Segment.size() > 7) ? pv1Segment.get(7) : null;

        if (providerField != null) {
            String[] providerParts = providerField.split("\\^");
            pv1Data.put("providerId", (providerParts.length > 0) ? providerParts[0] : null);
            pv1Data.put("LastName", (providerParts.length > 1) ? providerParts[1] : null);
            pv1Data.put("FirstName", (providerParts.length > 2) ? providerParts[2] : null);
            pv1Data.put("MiddleName", (providerParts.length > 3) ? providerParts[3] : null);
        } else {
            pv1Data.put("ID", null);
            pv1Data.put("LastName", null);
            pv1Data.put("FirstName", null);
            pv1Data.put("MiddleName", null);
        }
        pv1Data.put("providerName", providerField);

        pv1Data.put("externalVisitID", (pv1Segment.size() > 19) ? pv1Segment.get(19) : null);
        pv1Data.put("assignedLocation", (pv1Segment.size() > 3) ? pv1Segment.get(3) : null);

        System.out.println("Extracted PV1 Data: " + pv1Data);
        return pv1Data;
    }

    public Map<String, String> extractDataFromAIGSegment(List<String> aigSegment) {
        Map<String, String> aigData = new HashMap<>();
        System.out.println("AIG Segment: " + aigSegment);
        aigData.put("Set ID", (aigSegment.size() > 1) ? aigSegment.get(1) : null);

        if (aigSegment.size() > 3) {
            String resourceDetails = aigSegment.get(3);
            String[] resourceParts = resourceDetails.split("\\^");
            if (resourceParts.length > 0) {
                aigData.put("HL7 ID", resourceParts[0]);
            }
            if (resourceParts.length > 1) {
                aigData.put("Resource Last Name", resourceParts[1]);
            }
            if (resourceParts.length > 2) {
                aigData.put("Resource First Name", resourceParts[2]);
            }
        }

        aigData.put("Start Date/Time", (aigSegment.size() > 8) ? aigSegment.get(8) : null);
        aigData.put("Duration", (aigSegment.size() > 11) ? aigSegment.get(11) : null);
        aigData.put("Duration Units", (aigSegment.size() > 12) ? aigSegment.get(12) : null);
        System.out.println("Extracted AIG Data: " + aigData);

        return aigData;
    }

    public Map<String, String> extractDataFromAILSegment(List<String> ailSegment) {
        Map<String, String> ailData = new HashMap<>();
        System.out.println("AIL Segment: " + ailSegment);
        ailData.put("Set ID", (ailSegment.size() > 1) ? ailSegment.get(1) : null);

        if (ailSegment.size() > 3) {
            String resourceDetails = ailSegment.get(3);
            String[] resourceParts = resourceDetails.split("\\^");
            if (resourceParts.length > 0) {
                ailData.put("Location HL7Id", resourceParts[0]);
            }
            if (resourceParts.length > 1) {
                ailData.put("Location Name", resourceParts[1]);
            }

        }
        return ailData;
    }
}
