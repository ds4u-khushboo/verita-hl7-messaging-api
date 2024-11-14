package com.example.hl7project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class AppointmentTextMessageDTO {
    private Long visitAppointmentId;
    private LocalDate appointmentDate;
    private String visitStatusCode;
    private Long textMessageId;
    private String typeCode;
    private LocalDateTime createdAt;
    private Long days;

    public AppointmentTextMessageDTO(String visitAppointmentId, String appointmentDate, String visitStatusCode,
                                     Long textMessageId, String typeCode, String createdAt, int days) {
        this.visitAppointmentId = Long.valueOf(visitAppointmentId);
        this.appointmentDate = LocalDate.parse(appointmentDate);
        this.visitStatusCode = visitStatusCode;
        this.textMessageId = textMessageId;
        this.typeCode = typeCode;
        this.createdAt = LocalDateTime.parse(createdAt);
        this.days = (long) days;
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
