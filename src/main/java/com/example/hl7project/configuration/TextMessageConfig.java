package com.example.hl7project.configuration;

import com.example.hl7project.service.TwillioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextMessageConfig {

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

    @Value("${APPOINTMENT_CANCELLATION}")
    private String cancellation;

    @Value("${APPOINTMENT_DELETION}")
    private String deletion;

    @Value("${NO_SHOW_REMINDER_2_WEEK_DAYS}")
    private Integer NoShowReminderTwoWeekDays;

    @Value("${NO_SHOW_REMINDER_4_WEEK_DAYS}")
    private Integer NoShowReminderFourWeekDays;

    @Value("${APPOINTMENT_NO_SHOW_REMINDER_2Weeks}")
    private String appointment2WeeksReminder;

    @Value("${APPOINTMENT_NO_SHOW_REMINDER_4Weeks}")
    private String appointment4WeeksReminder;

    @Value("${time_difference_multiple_appointment}")
    private String timeDifference;
    @Bean
    public TwillioService twilioService() {
        return new TwillioService();
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

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

    public String getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(String timeDifference) {
        this.timeDifference = timeDifference;
    }

    public void setAppNoShow(String appNoShow) {
        this.appNoShow = appNoShow;
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


    public String getAppointment2WeeksReminder() {
        return appointment2WeeksReminder;
    }

    public void setAppointment2WeeksReminder(String appointment2WeeksReminder) {
        this.appointment2WeeksReminder = appointment2WeeksReminder;
    }

    public String getAppointment4WeeksReminder() {
        return appointment4WeeksReminder;
    }

    public Integer getNoShowReminderTwoWeekDays() {
        return NoShowReminderTwoWeekDays;
    }

    public void setNoShowReminderTwoWeekDays(int noShowReminderTwoWeekDays) {
        NoShowReminderTwoWeekDays = noShowReminderTwoWeekDays;
    }

    public Integer getNoShowReminderFourWeekDays() {
        return NoShowReminderFourWeekDays;
    }

    public void setNoShowReminderFourWeekDays(int noShowReminderFourWeekDays) {
        NoShowReminderFourWeekDays = noShowReminderFourWeekDays;
    }

    public void setAppointment4WeeksReminder(String appointment4WeeksReminder) {
        this.appointment4WeeksReminder = appointment4WeeksReminder;
    }
}

