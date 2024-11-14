package com.example.hl7project.configuration;

import com.example.hl7project.service.AppointmentService;
import com.example.hl7project.service.TwillioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;
    //
    @Value("${twilio.fromNumber}")
    private String fromNumber;

    @Value("${APPOINTMENT_CREATION}")
    private String appCreation;

    @Value("${APPOINTMENT_MODIFICATION}")
    private String appModification;

    @Value("${APPOINTMENT_NO_SHOW}")
    private String appNoShow;

    @Value("APPOINTMENT_CANCELLATION")
    private String cancellation;

    @Value("APPOINTMENT_DELETION")
    private String deletion;
    @Bean
    public TwillioService twilioService() {
        return new TwillioService();
    }

    @Bean
    public AppointmentService appointmentService() {
        return new AppointmentService();
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getCancellation() {
        return cancellation;
    }

    public void setCancellation(String cancellation) {
        this.cancellation = cancellation;
    }

    public String getDeletion() {
        return deletion;
    }

    public void setDeletion(String deletion) {
        this.deletion = deletion;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    //
    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getAppCreation() {
        return appCreation;
    }

    public void setAppCreation(String appCreation) {
        this.appCreation = appCreation;
    }

    public String getAppModification() {
        return appModification;
    }

    public void setAppModification(String appModification) {
        this.appModification = appModification;
    }

    public String getAppNoShow() {
        return appNoShow;
    }

    public void setAppNoShow(String appNoShow) {
        this.appNoShow = appNoShow;
    }
}
