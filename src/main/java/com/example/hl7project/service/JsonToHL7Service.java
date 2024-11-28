package com.example.hl7project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToHL7Service{

    public static String convertJsonToHL7(String jsonString) {
        try {
            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonString);

            // Build MSH Segment
            StringBuilder hl7Message = new StringBuilder();
            hl7Message.append("MSH|^~\\&|")
                    .append(rootNode.path("sendingApplication").asText("DefaultApp")).append("|")
                    .append(rootNode.path("sendingFacility").asText("DefaultFacility")).append("|")
                    .append(rootNode.path("receivingApplication").asText("DefaultDestApp")).append("|")
                    .append(rootNode.path("receivingFacility").asText("DefaultDestFacility")).append("|")
                    .append(rootNode.path("dateTimeOfMessage").asText("20241128120000")).append("||")
                    .append(rootNode.path("messageType").asText("ADT^A01")).append("|")
                    .append(rootNode.path("messageControlId").asText("MSG12345")).append("|")
                    .append(rootNode.path("processingId").asText("P")).append("|")
                    .append(rootNode.path("versionId").asText("2.4")).append("\n");

            hl7Message.append("EVN|")
                    .append(rootNode.path("eventTypeCode").asText("A01")).append("|")
                    .append(rootNode.path("recordedDateTime").asText("20241128120000")).append("|")
                    .append(rootNode.path("plannedEventDateTime").asText("")).append("|\n");

            // Build PID Segment
            JsonNode patient = rootNode.path("patient");
            hl7Message.append("PID|||")
                    .append(patient.path("id").asText("12345")).append("||")
                    .append(patient.path("lastName").asText("Doe")).append("^")
                    .append(patient.path("firstName").asText("John")).append("^")
                    .append(patient.path("middleName").asText("M")).append("||")
                    .append(patient.path("dob").asText("19800101")).append("|")
                    .append(patient.path("sex").asText("M")).append("|||")
                    .append(patient.path("address").path("street").asText("")).append("^")
                    .append(patient.path("address").path("city").asText("")).append("^")
                    .append(patient.path("address").path("state").asText("")).append("^")
                    .append(patient.path("address").path("zip").asText("")).append("||")
                    .append(patient.path("phone").asText("1234567890")).append("\n");


            // Build PV1 Segment
            JsonNode visit = rootNode.path("visit");
            hl7Message.append("PV1||")
                    .append(visit.path("patientClass").asText("O")).append("|")
                    .append(visit.path("assignedLocation").path("pointOfCare").asText("")).append("^")
                    .append(visit.path("assignedLocation").path("room").asText("")).append("^")
                    .append(visit.path("assignedLocation").path("bed").asText("")).append("|||")
                    .append(visit.path("attendingDoctor").path("id").asText("")).append("^")
                    .append(visit.path("attendingDoctor").path("lastName").asText("")).append("^")
                    .append(visit.path("attendingDoctor").path("firstName").asText("")).append("|")
                    .append("||||||||||||")
                    .append(visit.path("visitNumber").asText("VN12345")).append("|")
                    .append("|||||||||||||||||")
                    .append(visit.path("admitDate").asText("20241128")).append("\n");

            JsonNode insurance = rootNode.path("insurance");
            if (!insurance.isMissingNode()) {
                hl7Message.append("IN1|1|")
                        .append(insurance.path("planId").asText("")).append("|")
                        .append(insurance.path("companyName").asText("")).append("|")
                        .append(insurance.path("address").path("street").asText("")).append("^")
                        .append(insurance.path("address").path("city").asText("")).append("^")
                        .append(insurance.path("address").path("state").asText("")).append("^")
                        .append(insurance.path("address").path("zip").asText("")).append("|||")
                        .append(insurance.path("policyNumber").asText("")).append("\n");
            }

            return hl7Message.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // Example JSON input
        String jsonString = """
        {
            "sendingApplication": "TestApp",
            "sendingFacility": "TestFacility",
            "receivingApplication": "DestApp",
            "receivingFacility": "DestFacility",
            "dateTimeOfMessage": "20241128120000",
            "messageControlId": "MSG12345",
            "patient": {
                "id": "12345",
                "firstName": "John",
                "lastName": "Doe",
                "dob": "19800101",
                "sex": "M"
            },
            "visit": {
                "patientClass": "O",
                "visitNumber": "VN12345",
                "admitDate": "20241128"
            }
        }
        """;

        // Convert JSON to HL7
        String hl7Message = convertJsonToHL7(jsonString);
        System.out.println("Generated HL7 Message:");
        System.out.println(hl7Message);
    }
}
