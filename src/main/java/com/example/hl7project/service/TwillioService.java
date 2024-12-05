package com.example.hl7project.service;

import com.example.hl7project.configuration.TextMessageConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwillioService {

    @Autowired
    private TextMessageConfig twilioConfig;

    public Message getTwilioService(String body, String number) {
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
        //String smsBody = "Your appointment has been rescheduled to " + startDate.toString() + " at " + location + ". Reason: " + changeReason;

        return Message.creator(new com.twilio.type.PhoneNumber("+" + number),
                new PhoneNumber(twilioConfig.getFromNumber()), body).create();
    }

    public void sendSms(String to, String messageBody) {
        try {
            Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

            // Create and send the message
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber("+18562882592"),
                    messageBody
            ).create();

            // Log the SID of the message for tracking
            System.out.println("Message sent: " + message.getSid());
        } catch (Exception e) {
            // Handle any exceptions that may occur
            System.err.println("Failed to send SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String phoneNumber, String messageText) {
        try {
            Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
            // Implement Twilio API integration here
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioConfig.getFromNumber()),
                    messageText
            ).create();

            System.out.println("Message sent to: " + phoneNumber);
            return message.getStatus() != Message.Status.FAILED;
        } catch (Exception e) {
            System.err.println("Error sending message to " + phoneNumber + ": " + e.getMessage());
            return false;
        }
    }
}

