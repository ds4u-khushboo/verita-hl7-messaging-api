package com.example.hl7project.dto;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PatientSummaryDTO {

    private Long patientId;
    private String name;
    private String address;
    private LocalDate dateOfBirth;
    private String sex;
    private long appointmentCount;
    private long messagesTriggered;
    private List<BookingInfoDTO.AppointmentDTO> appointments;

    public PatientSummaryDTO(Patient patient, long appointmentCount, long messagesTriggered, List<Appointment> appointments) {
        this.patientId = Long.valueOf(patient.getPatientId());
        this.name = patient.getName();
        this.address = patient.getAddress();
        this.dateOfBirth = LocalDate.parse(patient.getDateOfBirth());
        this.sex = patient.getSex();
        this.appointmentCount = appointmentCount;
        this.messagesTriggered = messagesTriggered;
        this.appointments = appointments.stream()
                .map(appointment -> new BookingInfoDTO.AppointmentDTO(appointment))
                .collect(Collectors.toList());
    }

}
