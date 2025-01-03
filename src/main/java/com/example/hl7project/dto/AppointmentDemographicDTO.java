package com.example.hl7project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDemographicDTO {

    private String patientId;
    private String name;
    private String address;
    private String dateOfBirth;
    private String gender;
    private String visitStatusCode;
    private String appointmentDate;
    private Long visitAppointmentId;
    private String appointmentReason;
    private Long appointmentCount;
    private Long messagesTriggered;
    private Integer age;

    public AppointmentDemographicDTO(Map<String, Object> fieldMap) {
    }
}
