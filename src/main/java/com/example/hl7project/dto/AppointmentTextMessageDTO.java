package com.example.hl7project.dto;

import com.example.hl7project.utility.ConfirmationMessageStatus;
import com.example.hl7project.utility.ReminderMessageStatus;

import java.time.LocalDateTime;


public class AppointmentTextMessageDTO {
    private Long visitAppointmentId;

    private String patientId;
    private LocalDateTime appointmentDate;
    private String providerCode;
    private String visitStatusCode;
    private Integer days;

    private ConfirmationMessageStatus ConfirmationMessageStatus;

    private ReminderMessageStatus reminderMessageStatus;

    public AppointmentTextMessageDTO() {

    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public Long getVisitAppointmentId() {
        return visitAppointmentId;
    }

    public void setVisitAppointmentId(Long visitAppointmentId) {
        this.visitAppointmentId = visitAppointmentId;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getVisitStatusCode() {
        return visitStatusCode;
    }

    public void setVisitStatusCode(String visitStatusCode) {
        this.visitStatusCode = visitStatusCode;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public com.example.hl7project.utility.ConfirmationMessageStatus getConfirmationMessageStatus() {
        return ConfirmationMessageStatus;
    }

    public void setConfirmationMessageStatus(com.example.hl7project.utility.ConfirmationMessageStatus confirmationMessageStatus) {
        ConfirmationMessageStatus = confirmationMessageStatus;
    }

    public ReminderMessageStatus getReminderMessageStatus() {
        return reminderMessageStatus;
    }

    public void setReminderMessageStatus(ReminderMessageStatus reminderMessageStatus) {
        this.reminderMessageStatus = reminderMessageStatus;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
