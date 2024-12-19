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
            Map<String, String> patientDetails = hl7UtilityService.extractPatientDetailsFromJson(appointmentRequest);
            String firstName = patientDetails.get("firstName");
            String lastName = patientDetails.get("lastName");
            String dateOfBirth = patientDetails.get("dob");

            System.out.println("Checking patient with details: firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dateOfBirth);
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
        Map<String, String> patientData = new HashMap<>();
        patientData.put("External Patient ID", "");
       // patientData.put("External Patient MRN", appointmentRequest.patient.getMrnNo());
        patientData.put("Patient Name", firstName + " " + lastName);
        patientData.put("Date of Birth", dateOfBirth);
        patientData.put("Sex", appointmentRequest.getPatient().getSex());
        patientData.put("Patient Address", appointmentRequest.getPatient().getAddress().toString());
        patientData.put("Home Phone Number", appointmentRequest.getPatient().getPhone());
        patientData.put("First Name",firstName);
        patientData.put("Last Name",lastName);
        patientService.savePatientData(patientData);
        System.out.println("New patient details saved to database.");

        String adtHl7Message = hl7UtilityService.buildADTHL7Message(appointmentRequest);
        System.out.println("Sending ADT HL7 message to /send-adt-message: " + adtHl7Message);
        boolean adtSuccess = sendADTHL7MessageToMirth(adtHl7Message);

        if (adtSuccess) {
            String siuHl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
            System.out.println("Sending SIU HL7 message to Mirth: " + siuHl7Message);
            sendSIUHL7MessageToMirth(siuHl7Message);
        } else {
            System.out.println("ADT HL7 message failed. SIU message will not be sent.");
        }
    }

    private void handleExistingPatient(AppointmentRequest appointmentRequest) throws Exception {
        String siuHl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
        System.out.println("Patient already exists. Sending SIU HL7 message to Mirth: " + siuHl7Message);
        sendSIUHL7MessageToMirth(siuHl7Message);
    }

    private boolean sendADTHL7MessageToMirth(String hl7Message) throws Exception {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(textMessageConfig.getMirthOutboundADTEndpoint().trim()))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("Message sent successfully to " + response);
            return true;
        } else {
            System.out.println("Failed to send message to " + response + " with status: " + response.statusCode());
            return false;
        }
    }

    private void sendSIUHL7MessageToMirth(String hl7Message) throws Exception {
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
