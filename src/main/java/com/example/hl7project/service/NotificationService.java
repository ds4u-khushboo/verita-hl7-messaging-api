package com.example.hl7project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private TwillioService twillioService;

    public void sendAppointmentNotification(String message,String number) {
        twillioService.getTwilioService(message,number);
    }

    public void sendPatientCreateNotification(String phoneNumber, String message) {
        twillioService.getTwilioService(message, "91" + phoneNumber);
    }
    public void sendNoShowNotification(String phoneNumber, String message) {
        twillioService.getTwilioService(message+"ns", "91" +phoneNumber);
    }
}
