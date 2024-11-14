package com.example.hl7project.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "appointment_date")
    private String appointmentDate;

    @Column(name = "appointment_time")
    private String appointmentTime;

    @Column(name = "appointment_datetime")
    private String appointmentDatetime;

    @Column(name = "appointment_reason", length = 255)
    private String appointmentReason;

    @Column(name = "visit_status_code", length = 20)
    private String visitStatusCode;

    @Column(name = "resource_name", length = 100)
    private String resourceName;

    @Column(name = "duration", length = 255)
    private String duration;

    @Column(name = "duration_units", length = 20)
    private String durationUnits;

    @Column(name = "appointment_type", length = 50)
    private String appointmentType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "start_time", length = 50)
    private String startTime;
    @Column(name = "is_confirm_request_sent")
    private Boolean isConfirmRequestSent;
    @Column(name = "is_confirm_request_replied")
    private Boolean isConfirmRequestReplied;
    @Column(name = "is_confirmed")
    private Boolean isConfirmed;
    @Column(name = "external_patient_id", length = 255)
    private String externalPatientId;

    @Column(name = "visit_appointment_id", length = 255, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private String visitAppointmentId;

    @Column(name = "sms_sent_status")
    private Integer smsSentStatus;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "provider_code")  // Foreign key column in the 'Appointment' table
    private Providers providers;
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private List<MessageEntity> inboundMessages;

    public Appointment() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentReason() {
        return appointmentReason;
    }

    public String getAppointmentDatetime() {
        return appointmentDatetime;
    }

    public void setAppointmentDatetime(String appointmentDatetime) {
        this.appointmentDatetime = appointmentDatetime;
    }

    public void setAppointmentReason(String appointmentReason) {
        this.appointmentReason = appointmentReason;
    }

    public boolean isConfirmRequestSent() {
        return isConfirmRequestSent;
    }

    public void setConfirmRequestSent(boolean confirmRequestSent) {
        isConfirmRequestSent = confirmRequestSent;
    }

    public boolean isConfirmRequestReplied() {
        return isConfirmRequestReplied;
    }

    public void setConfirmRequestReplied(boolean confirmRequestReplied) {
        isConfirmRequestReplied = confirmRequestReplied;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public String getVisitStatusCode() {
        return visitStatusCode;
    }

    public void setVisitStatusCode(String visitStatusCode) {
        this.visitStatusCode = visitStatusCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationUnits() {
        return durationUnits;
    }

    public void setDurationUnits(String durationUnits) {
        this.durationUnits = durationUnits;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getConfirmRequestSent() {
        return isConfirmRequestSent;
    }

    public void setConfirmRequestSent(Boolean confirmRequestSent) {
        isConfirmRequestSent = confirmRequestSent;
    }

    public Boolean getConfirmRequestReplied() {
        return isConfirmRequestReplied;
    }

    public void setConfirmRequestReplied(Boolean confirmRequestReplied) {
        isConfirmRequestReplied = confirmRequestReplied;
    }

    public Boolean getConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        isConfirmed = confirmed;
    }

    public Providers getProviders() {
        return providers;
    }

    public void setProviders(Providers providers) {
        this.providers = providers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getExternalPatientId() {
        return externalPatientId;
    }

    public void setExternalPatientId(String externalPatientId) {
        this.externalPatientId = externalPatientId;
    }

    public String getVisitAppointmentId() {
        return visitAppointmentId;
    }

    public void setVisitAppointmentId(String visitAppointmentId) {
        this.visitAppointmentId = visitAppointmentId;
    }

    public Integer getSmsSentStatus() {
        return smsSentStatus;
    }

    public void setSmsSentStatus(Integer smsSentStatus) {
        this.smsSentStatus = smsSentStatus;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<MessageEntity> getInboundMessages() {
        return inboundMessages;
    }

    public void setInboundMessages(List<MessageEntity> inboundMessages) {
        this.inboundMessages = inboundMessages;
    }

    public void setLastMessageSentDate(LocalDate now) {
    }
// Getters and Setters
}
