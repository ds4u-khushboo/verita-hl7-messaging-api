package com.example.hl7project.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.parser.PipeParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class mirthService {

    private static final String CONVERT_API_URL = "http://localhost:8083/hl7/convert";
    private static final String MIRTH_SIU_OUTBOUND_URL = "http://localhost:8082/hl7/siu/outbound";

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    public void HL7MessageService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Method to call the convert API and send the response to Mirth
    public void convertAndForwardHL7Message(Map<String, Object> hl7RequestData) throws Exception {
        // Step 1: Call the convert API
        HttpRequest convertRequest = HttpRequest.newBuilder()
                .uri(URI.create(CONVERT_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(hl7RequestData)))
                .build();

        HttpResponse<String> convertResponse = httpClient.send(convertRequest, HttpResponse.BodyHandlers.ofString());

        if (convertResponse.statusCode() == 200) {
            String hl7Message = convertResponse.body();
            System.out.println("Received HL7 message from /convert: " + hl7Message);

            // Step 2: Forward the HL7 message to Mirth SIU outbound API
            sendToMirth(hl7Message);
        } else {
            System.out.println("Failed to convert message. Status code: " + convertResponse.statusCode());
        }
    }

    // Method to forward HL7 message to Mirth
    private void sendToMirth(String hl7Message) throws Exception {
        HttpRequest mirthRequest = HttpRequest.newBuilder()
                .uri(URI.create(MIRTH_SIU_OUTBOUND_URL))
                .header("Content-Type", "application/hl7-v2")
                .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
                .build();

        HttpResponse<String> mirthResponse = httpClient.send(mirthRequest, HttpResponse.BodyHandlers.ofString());

        if (mirthResponse.statusCode() == 200) {
            System.out.println("Successfully sent HL7 message to Mirth: " + mirthResponse.body());
        } else {
            System.out.println("Failed to send HL7 message to Mirth. Status code: " + mirthResponse.statusCode());
        }
    }

//    public static void main(String[] args) {


    public static void main(String[] args) {
        // Example JSON Input
        String jsonInput = """
                {
                    "sendingApplication": "TestApp",
                    "sendingFacility": "TestFacility",
                    "receivingApplication": "DestApp",
                    "receivingFacility": "DestFacility",
                    "patientId": "12345",
                    "patientName": {
                        "lastName": "Doe",
                        "firstName": "John"
                    },
                    "dateOfBirth": "19800101",
                    "gender": "M",
                    "admitDate": "20241128",
                    "visitNumber": "VN12345"
                }
                """;

        try {
            // Parse JSON Input
            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonInput);

            // Convert JSON to HL7
            String hl7Message = convertJsonToHL7(rootNode);
            System.out.println("Generated HL7 Message:");
            System.out.println(hl7Message);

        } catch (IOException | HL7Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertJsonToHL7(JsonNode jsonNode) throws HL7Exception, IOException {
        // Create ADT_A01 Message
        ADT_A01 adtMessage = new ADT_A01();
        adtMessage.initQuickstart("ADT", "A01", "P");

        // Populate MSH Segment
        MSH msh = adtMessage.getMSH();
        msh.getSendingApplication().getNamespaceID().setValue(jsonNode.get("sendingApplication").asText());
        msh.getSendingFacility().getNamespaceID().setValue(jsonNode.get("sendingFacility").asText());
        msh.getReceivingApplication().getNamespaceID().setValue(jsonNode.get("receivingApplication").asText());
        msh.getReceivingFacility().getNamespaceID().setValue(jsonNode.get("receivingFacility").asText());
        msh.getDateTimeOfMessage().getTime().setValue("20241128120000"); // Current datetime
        msh.getMessageControlID().setValue("MSG12345");
        msh.getProcessingID().getProcessingID().setValue("P");
        msh.getVersionID().getVersionID().setValue("2.4");

        // Populate PID Segment
        PID pid = adtMessage.getPID();
        pid.getPatientID().getIDNumber().setValue(jsonNode.get("patientId").asText());
        pid.getPatientName(0).getFamilyName().getSurname().setValue(jsonNode.get("patientName").get("lastName").asText());
        pid.getPatientName(0).getGivenName().setValue(jsonNode.get("patientName").get("firstName").asText());
        pid.getDateTimeOfBirth().getTime().setValue(jsonNode.get("dateOfBirth").asText());
        pid.getAdministrativeSex().setValue(jsonNode.get("gender").asText());

        // Populate PV1 Segment
        PV1 pv1 = adtMessage.getPV1();
        pv1.getPatientClass().setValue("O"); // Outpatient
        pv1.getVisitNumber().getIDNumber().setValue(jsonNode.get("visitNumber").asText());
        pv1.getAdmitDateTime().getTime().setValue(jsonNode.get("admitDate").asText());
        // Ensure all segments are included by explicitly setting them
        adtMessage.getMSH();
        adtMessage.getPID();
        adtMessage.getPV1();
       // adtMessage.getValidationContext();
        // Convert HL7 Message to String
        PipeParser parser = new PipeParser();
        return parser.encode(adtMessage);
    }
}


