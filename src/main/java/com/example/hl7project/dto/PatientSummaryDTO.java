package com.example.hl7project.dto;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(long appointmentCount) {
        this.appointmentCount = appointmentCount;
    }

    public long getMessagesTriggered() {
        return messagesTriggered;
    }

    public void setMessagesTriggered(long messagesTriggered) {
        this.messagesTriggered = messagesTriggered;
    }

    public List<BookingInfoDTO.AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<BookingInfoDTO.AppointmentDTO> appointments) {
        this.appointments = appointments;
    }
}
