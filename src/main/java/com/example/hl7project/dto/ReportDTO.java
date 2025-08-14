package com.example.hl7project.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportDTO {

    private Long appointmentId;
    private LocalDateTime appointmentDate;
    private String status;
    private String patientFullName;
    private String patientEmail;
    private String patientPhoneNumber;
    private String doctorFullName;
    private String doctorSpecialty;
    private List<MessageDTO> messages;

}
