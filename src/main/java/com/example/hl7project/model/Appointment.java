package com.example.hl7project.model;
import com.example.hl7project.utility.ConfirmationMessageStatus;
import com.example.hl7project.utility.ReminderMessageStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "appointment_date")
    private String appointmentDate;

    @Column(name = "appointment_date_utc")
    private LocalDate appointmentDateUtc;

    @Column(name = "appointment_time")
    private String appointmentTime;

    @Column(name = "appointment_datetime")
    private String appointmentDatetime;

    @Column(name = "appointment_reason", length = 255)
    private String appointmentReason;

    @Column(name = "visit_status_code", length = 20)
    private String visitStatusCode;

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

    @Column(name = "cm_code")
    private String cmCode;

    @Column(name = "is_confirm_request_sent")
    private Boolean isConfirmRequestSent;
    @Column(name = "is_confirm_request_replied")
    private Boolean isConfirmRequestReplied;
    @Column(name = "is_confirmed")
    private Boolean isConfirmed;
    @Column(name = "external_patient_id", length = 255)
    private String externalPatientId;

    public LocalDate getAppointmentDateUtc() {
        return appointmentDateUtc;
    }

    public void setAppointmentDateUtc(LocalDate appointmentDateUtc) {
        this.appointmentDateUtc = appointmentDateUtc;
    }

    public ReminderMessageStatus getReminderMessageStatus() {
        return reminderMessageStatus;
    }

    public void setReminderMessageStatus(ReminderMessageStatus reminderMessageStatus) {
        this.reminderMessageStatus = reminderMessageStatus;
    }

    public com.example.hl7project.utility.ConfirmationMessageStatus getConfirmationMessageStatus() {
        return ConfirmationMessageStatus;
    }

    public void setConfirmationMessageStatus(com.example.hl7project.utility.ConfirmationMessageStatus confirmationMessageStatus) {
        ConfirmationMessageStatus = confirmationMessageStatus;
    }

    @Column(name = "visit_appointment_id")
    private Long visitAppointmentId;
    @Column(name = "sms_sent_status")
    private Integer smsSentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_message_status", columnDefinition = "varchar(255) default 'NONE'")
    private ReminderMessageStatus reminderMessageStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmation_message_status", columnDefinition = "varchar(255) default 'NONE'")
    private com.example.hl7project.utility.ConfirmationMessageStatus ConfirmationMessageStatus;
    @ManyToOne
    @JoinColumn(name = "external_patient_id", referencedColumnName = "external_patient_id", nullable = false, insertable = false, updatable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Providers providers;
//    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
//    private List<InboundHL7Message> inboundMessages;

    public Appointment() {

    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

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

    public String getCmCode() {
        return cmCode;
    }

    public void setCmCode(String cmCode) {
        this.cmCode = cmCode;
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

    public String getExternalPatientId() {
        return externalPatientId;
    }

    public void setExternalPatientId(String externalPatientId) {
        this.externalPatientId = externalPatientId;
    }

    public Long getVisitAppointmentId() {
        return visitAppointmentId;
    }

    public void setVisitAppointmentId(Long visitAppointmentId) {
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

//    public List<InboundHL7Message> getInboundMessages() {
//        return inboundMessages;
//    }
//
//    public void setInboundMessages(List<InboundHL7Message> inboundMessages) {
//        this.inboundMessages = inboundMessages;
//    }

    public void setLastMessageSentDate(LocalDate now) {
    }

    public void setId(long l) {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
// Getters and Setters
}
