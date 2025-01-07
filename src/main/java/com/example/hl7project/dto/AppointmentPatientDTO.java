package com.example.hl7project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Getter
@Setter
public class AppointmentPatientDTO {
    @JsonProperty("patientId")
    private String patientId;

    @JsonProperty("patientName")
    private String patientName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("visitStatus")
    private String visitStatus;

    @JsonProperty("appointmentDate")
    private String appointmentDate;

    @JsonProperty("visitAppointmentId")
    private Long visitAppointmentId;

    @JsonProperty("appointmentReason")
    private String appointmentReason;

    @JsonProperty("providerName")
    private String providerName;

    @JsonProperty("specialty")
    private String specialty;

    @JsonProperty("messageTriggered")
    private Long messageTriggered;

    @JsonProperty("countOfAppointments")
    private Long countOfAppointments;

    @JsonProperty("age")
    private Long age;

    public AppointmentPatientDTO() {
    }

    public AppointmentPatientDTO(String patientId, String patientName, String address, String dateOfBirth,
                                 String sex, String visitStatus, String appointmentDate,
                                 Long visitAppointmentId, String appointmentReason,
                                 Long countOfAppointments, Long messageTriggered, Long age) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.visitStatus = visitStatus;
        this.appointmentDate = appointmentDate;
        this.visitAppointmentId = visitAppointmentId;
        this.appointmentReason = appointmentReason;
        this.countOfAppointments = countOfAppointments;
        this.messageTriggered = messageTriggered;
        this.age = age;
    }

    public AppointmentPatientDTO(String patientId, String patientName, String address, String dateOfBirth, String sex,
                                 String visitStatus, String appointmentDate, Long visitAppointmentId,
                                 String appointmentReason, String providerName, String specialty,
                                 Long messageTriggered, Long countOfAppointments, Long age) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.visitStatus = visitStatus;
        this.appointmentDate = appointmentDate;
        this.visitAppointmentId = visitAppointmentId;
        this.appointmentReason = appointmentReason;
        this.providerName = providerName;
        this.specialty = specialty;
        this.messageTriggered = messageTriggered;
        this.countOfAppointments = countOfAppointments;
        this.age = age;
    }
}
