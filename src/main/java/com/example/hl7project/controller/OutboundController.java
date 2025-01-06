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
}
