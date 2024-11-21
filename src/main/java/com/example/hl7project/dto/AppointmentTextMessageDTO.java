package com.example.hl7project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class AppointmentTextMessageDTO {
    private Long visitAppointmentId;

    private String externalPatientId;
    private LocalDate appointmentDate;
    private String visitStatusCode;
    private Long textMessageId;
    private String typeCode;
    private LocalDateTime createdAt;
    private Long days;

//    public AppointmentTextMessageDTO(Long visitAppointmentId, String externalPatientId, String appointmentDate, String visitStatusCode, Long textMessageId, String typeCode, LocalDateTime createdAt, Long days) {
//        this.visitAppointmentId = visitAppointmentId;
//        this.externalPatientId = externalPatientId;
//        this.appointmentDate = appointmentDate;
//        this.visitStatusCode = visitStatusCode;
//        this.textMessageId = textMessageId;
//        this.typeCode = typeCode;
//        this.createdAt = createdAt;
//        this.days = days;
//    }

    public AppointmentTextMessageDTO() {

    }

    public Long getVisitAppointmentId() {
        return visitAppointmentId;
    }

    public void setVisitAppointmentId(Long visitAppointmentId) {
        this.visitAppointmentId = visitAppointmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getVisitStatusCode() {
        return visitStatusCode;
    }

    public void setVisitStatusCode(String visitStatusCode) {
        this.visitStatusCode = visitStatusCode;
    }

    public Long getTextMessageId() {
        return textMessageId;
    }

    public void setTextMessageId(Long textMessageId) {
        this.textMessageId = textMessageId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getExternalPatientId() {
        return externalPatientId;
    }

    public void setExternalPatientId(String externalPatientId) {
        this.externalPatientId = externalPatientId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }
}
