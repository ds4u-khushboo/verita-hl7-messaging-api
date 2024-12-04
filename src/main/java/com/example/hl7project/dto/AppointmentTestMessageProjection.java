package com.example.hl7project.dto;

import java.sql.Timestamp;

public class AppointmentTestMessageProjection {
    private Long VisitAppointmentId;
    private String ExternalPatientId;
    private  String CmCode;
    private Timestamp CreatedAt;
    private Integer MinutesElapsed;
    private  Integer IsPreviousNew;

    public AppointmentTestMessageProjection(Long visitAppointmentId, String externalPatientId, String cmCode, Timestamp createdAt, Integer minutesElapsed, Integer isPreviousNew) {
        VisitAppointmentId = visitAppointmentId;
        ExternalPatientId = externalPatientId;
        CmCode = cmCode;
        CreatedAt = createdAt;
        MinutesElapsed = minutesElapsed;
        IsPreviousNew = isPreviousNew;
    }


    public Long getVisitAppointmentId() {
        return VisitAppointmentId;
    }

    public String getExternalPatientId() {
        return ExternalPatientId;
    }

    public String getCmCode() {
        return CmCode;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public Integer getMinutesElapsed() {
        return MinutesElapsed;
    }

    public Integer getIsPreviousNew() {
        return IsPreviousNew;
    }
}
