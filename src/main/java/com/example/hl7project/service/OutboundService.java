package com.example.hl7project.service;

import com.example.hl7project.dto.AppointmentRequest;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OutboundService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private HL7UtilityService hl7UtilityService;

    public String processAppointmentRequest(AppointmentRequest appointmentRequest) {
        try {
            // Extract patient details from JSON
            Map<String, String> patientDetails = hl7UtilityService.extractPatientDetailsFromJson(appointmentRequest);
            String firstName = patientDetails.get("firstName");
            String lastName = patientDetails.get("lastName");
            String dateOfBirth = patientDetails.get("dob");
            String ssn = patientDetails.get("ssn");
            System.out.println("Checking patient with details: firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dateOfBirth);

            // Check if patient exists
            Optional<Patient> existingPatient = patientRepository.findPatientByDetails(firstName, lastName, dateOfBirth);

            if (existingPatient.isEmpty()) {
                handleNewPatient(appointmentRequest, firstName, lastName, dateOfBirth);
            } else {
                handleExistingPatient(appointmentRequest);
            }

            return "HL7 message processed successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing HL7 message: " + e.getMessage();
        }
    }

    private void handleNewPatient(AppointmentRequest appointmentRequest, String firstName, String lastName, String dateOfBirth) throws Exception {
        // Create a map to hold patient data
        Map<String, String> patientData = new HashMap<>();
        patientData.put("External Patient ID", appointmentRequest.patient.getMrnNo());
        patientData.put("External Patient MRN", appointmentRequest.patient.getMrnNo()); // Replace with appropriate field
        patientData.put("Patient Name", firstName + " " + lastName);
        patientData.put("Date of Birth", dateOfBirth);
        patientData.put("Sex", appointmentRequest.patient.getSex());
        // patientData.put("Race", appointmentRequest.patient.getRace());
        patientData.put("Patient Address", appointmentRequest.patient.getAddress().toString());
        patientData.put("Home Phone Number", appointmentRequest.patient.getPhone());
//        patientData.put("Primary Language", appointmentRequest.patient.getLanguage());
//        patientData.put("Marital Status", appointmentRequest.patient.getMaritalStatus());
        patientData.put("firstName", firstName);
        patientData.put("lastName", lastName);

        // Save patient data
        patientService.savePatientData(patientData);

        System.out.println("New patient details saved to database.");

        // Convert JSON to HL7 message and send it to Mirth
        String adtHl7Message = hl7UtilityService.convertJsonToADTHL7(appointmentRequest);
        System.out.println("ADTHL7Message: " + adtHl7Message);
        sendHL7MessageToMirth(adtHl7Message);

        // Send SIU HL7 message to Mirth
        String siuHl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
        System.out.println("SIUHL7Message: " + siuHl7Message);
        sendHL7MessageToMirth(siuHl7Message);
    }

    private void handleExistingPatient(AppointmentRequest appointmentRequest) throws Exception {
        // Build SIU HL7 message and send it to Mirth
        String hl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
        sendHL7MessageToMirth(hl7Message);
        System.out.println("Patient already exists. SIU HL7 message sent to Mirth.");
    }

    private void sendHL7MessageToMirth(String hl7Message) throws Exception {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8085/"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        handleHttpResponse(response);
    }

    private void handleHttpResponse(HttpResponse<String> response) {
        if (response.statusCode() == 302) {
            response.headers().firstValue("Location").ifPresent(redirectUrl ->
                    System.out.println("Redirecting to: " + redirectUrl));
        } else if (response.statusCode() == 200) {
            System.out.println("Request successful!");
        } else {
            System.out.println("Failed with status code: " + response.statusCode());
        }

        System.out.println("Response body: " + response.body());
    }
}
