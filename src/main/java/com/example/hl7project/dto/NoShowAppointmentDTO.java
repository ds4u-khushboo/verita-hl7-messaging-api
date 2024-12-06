package com.example.hl7project.dto;


public interface NoShowAppointmentDTO {
    Long getAppointmentId();
    String getPatientId();
    String getAppointmentDateUtc();
    Long getProviderId();
    String getSpecialty();
    String getVisitStatusCode();
    String getReminderMessageStatus();
    Integer getDays();
}

