package com.example.hl7project.controller;

import com.example.hl7project.dto.AppointmentRequest;
import com.example.hl7project.service.HL7UtilityService;
import com.example.hl7project.service.OutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@RestController
public class OutboundController {

    @Autowired
    private OutboundService outboundService;

    @Autowired
    private HL7UtilityService hl7UtilityService;

    @PostMapping("/book-appointment")
    public String conversion(@RequestBody AppointmentRequest appointmentRequest) {
        return outboundService.processAppointmentRequest(appointmentRequest);
    }

    @PostMapping("/siubuild")
    public String buildSIU(@RequestBody AppointmentRequest appointmentRequest) throws Exception {
        String hl7Message = hl7UtilityService.buildSIUHl7Message(appointmentRequest);
        System.out.println("hl7Message::" + hl7Message);
        return hl7Message.toString();
    }

    @PostMapping("/sendTcp")
    public ResponseEntity<String> sendHL7MessageTcp(@RequestBody String hl7Message) {
        String mirthTcpHost = "localhost";
        int mirthTcpPort = 6661;

        String startOfMessage = "\u000b";

        String endOfMessage = "\u001c" + "\r";
        String hl7MessageWithFrame = startOfMessage + hl7Message + endOfMessage;

        try (Socket socket = new Socket(mirthTcpHost, mirthTcpPort)) {
            socket.setSoTimeout(10000);
            try (OutputStream outStream = socket.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream))) {
                writer.write(hl7MessageWithFrame);
                writer.flush();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send message via TCP: " + e.getMessage());
            }

            String response = null;
            try (InputStream inStream = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inStream))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                response = responseBuilder.toString();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to read response from Mirth: " + e.getMessage());
            }

            if (response != null && !response.isEmpty()) {
                return ResponseEntity.ok("Message sent to Mirth TCP Listener. Response: " + response);
            } else {
                return ResponseEntity.ok("Message sent to Mirth TCP Listener, but no response received.");
            }

        } catch (SocketException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Connection aborted: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to connect or send message via TCP: " + e.getMessage());
        }
    }

//    @PostMapping("/sendHttp")
//    private void sendHL7MessageToMirth(@RequestBody String hl7Message) throws Exception {
//        if (hl7Message == null || hl7Message.isEmpty()) {
//            throw new IllegalArgumentException("HL7 message cannot be null or empty");
//        }
//
//        System.out.println("Sending HL7 message: " + hl7Message);  // Log the received message
//
//        HttpClient httpClient = HttpClient.newBuilder()
//                .followRedirects(HttpClient.Redirect.ALWAYS)
//                .build();
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8089/send-message/"))
//                .header("Content-Type", "text/plain")
//                .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
//                .build();
//
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() == 302) {
//            response.headers().firstValue("Location").ifPresent(redirectUrl ->
//                    System.out.println("Redirecting to: " + redirectUrl));
//        } else if (response.statusCode() == 200) {
//            System.out.println("Request successful!");
//        } else {
//            System.out.println("Failed with status code: " + response.statusCode());
//        }
//
//        System.out.println("Response body: " + response.body());
//    }


    private String sendHl7ToMirth(String hl7Message) throws Exception {
        String mirthEndpoint = "https://10.0.1.52:8443/api/channels/21ceec35-d53a-42cf-ab70-059353d21454?destinationMetaDataId=1";

        HttpClient httpClient = HttpClient.newBuilder().build();

        // Replace with your Mirth username and password
        String username = "admin";
        String password = "admin";
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        // Prepare HTTP POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(mirthEndpoint))
                .header("Content-Type", "text/plain")
                .header("Authorization", authHeader)
                .POST(HttpRequest.BodyPublishers.ofString(hl7Message))
                .build();

        // Send the request and receive the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Mirth Response: " + response.body());
            return "Successfully sent HL7 message to Mirth.";
        } else {
            System.err.println("Failed to send HL7 message. Status code: " + response.statusCode());
            return "Failed to send HL7 message. Status code: " + response.statusCode();
        }
    }
}
