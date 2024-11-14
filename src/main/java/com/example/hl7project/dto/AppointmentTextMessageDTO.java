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

}
