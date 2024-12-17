package com.example.hl7project.service;

import com.example.hl7project.configuration.TextMessageConfig;
import com.example.hl7project.dto.AppointmentRequest;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private TextMessageConfig textMessageConfig;

    public String processAppointmentRequest(AppointmentRequest appointmentRequest) {
        try {
            // Extract patient details from the appointment request
            Map<String, String> patientDetails = hl7UtilityService.extractPatientDetailsFromJson(appointmentRequest);
            String firstName = patientDetails.get("firstName");
            String lastName = patientDetails.get("lastName");
            String dateOfBirth = patientDetails.get("dob");

            System.out.println("Checking patient with details: firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dateOfBirth);

            // Check if the patient exists in the database
            Optional<Patient> existingPatient = patientRepository.findPatientByDetails(firstName, lastName, dateOfBirth);

            if (existingPatient.isEmpty()) {
                // If patient doesn't exist, handle as a new patient and send ADT and SIU messages
                handleNewPatient(appointmentRequest, firstName, lastName, dateOfBirth);
            } else {
                // If patient exists, send only SIU message
                handleExistingPatient(appointmentRequest);
            }

            return "HL7 message processed successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing HL7 message: " + e.getMessage();
        }
    }

    private void handleNewPatient(AppointmentRequest appointmentRequest, String firstName, String lastName, String dateOfBirth) throws Exception {
        // Prepare and save patient data to the database
        Map<String, String> patientData = new HashMap<>();
        patientData.put("External Patient ID", "");
       // patientData.put("External Patient MRN", appointmentRequest.patient.getMrnNo());
        patientData.put("Patient Name", firstName + " " + lastName);
        patientData.put("Date of Birth", dateOfBirth);
        patientData.put("Sex", appointmentRequest.patient.getSex());
        patientData.put("Patient Address", appointmentRequest.patient.getAddress().toString());
        patientData.put("Home Phone Number", appointmentRequest.patient.getPhone());
        patientData.put("First Name",firstName);
        patientData.put("Last Name",lastName);
        // Save patient data to the database
        patientService.savePatientData(patientData);
        System.out.println("New patient details saved to database.");

        // Convert AppointmentRequest to ADT HL7 message and send it to /send-adt-message
        String adtHl7Message = hl7UtilityService.buildADTHL7Message(appointmentRequest);
        System.out.println("Sending ADT HL7 message to /send-adt-message: " + adtHl7Message);
        boolean adtSuccess = sendADTHL7MessageToMirth(adtHl7Message);

        if (adtSuccess) {
            // After ADT message is successfully sent, convert AppointmentRequest to SIU HL7 message and send to Mirth
            String siuHl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
            System.out.println("Sending SIU HL7 message to Mirth: " + siuHl7Message);
            sendSIUHL7MessageToMirth(siuHl7Message);
        } else {
            System.out.println("ADT HL7 message failed. SIU message will not be sent.");
        }
    }

    private void handleExistingPatient(AppointmentRequest appointmentRequest) throws Exception {
        // If patient exists, only send SIU HL7 message to Mirth channel
        String siuHl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
        System.out.println("Patient already exists. Sending SIU HL7 message to Mirth: " + siuHl7Message);
        sendSIUHL7MessageToMirth(siuHl7Message);
    }

    private boolean sendADTHL7MessageToMirth(String hl7Message) throws Exception {
        // Send HL7 message to the specified HTTP listener endpoint
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(textMessageConfig.getMirthOutboundADTEndpoint().trim()))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Handle the response from the listener
        if (response.statusCode() == 200) {
            System.out.println("Message sent successfully to " + response);
            return true;
        } else {
            System.out.println("Failed to send message to " + response + " with status: " + response.statusCode());
            return false;
        }
    }

    private void sendSIUHL7MessageToMirth(String hl7Message) throws Exception {
        // Send HL7 SIU message to Mirth channel
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(textMessageConfig.getMirthOutboundSIUEndpoint().trim()))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        handleHttpResponse(response);
    }

    private void handleHttpResponse(HttpResponse<String> response) {
        // Handle the response from the Mirth server
        if (response.statusCode() == 302) {
            response.headers().firstValue("Location").ifPresent(redirectUrl ->
                    System.out.println("Redirecting to: " + redirectUrl));
        } else if (response.statusCode() == 200) {
            System.out.println("Request successful!");
        } else {
            System.out.println("Failed with status code: " + response.statusCode());
        }

        // Log the response body for debugging
        System.out.println("Response body: " + response.body());
    }
}
