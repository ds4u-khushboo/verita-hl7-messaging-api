package com.example.hl7project.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inbound_siu_message")
public class InboundHL7Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "raw_message", columnDefinition = "LONGTEXT")
    private String rawMessage;

    @Column(name = "message_type", length = 255)
    private String messageType;

    @Column(name = "message_text", length = 255)
    private String messageText;

    @Column(name = "phone_number", length = 255)
    private String phoneNumber;

    @Column(name = "sent_at", length = 255)
    private String sentAt;

    @Column(name = "visit_appointment_id", length = 45)
    private String visitAppointmentId;

    @Column(name = "patient_id", length = 45)
    private String patientId;


    public InboundHL7Message() {

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getHomePhone() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getVisitAppointmentId() {
        return visitAppointmentId;
    }

    public void setVisitAppointmentId(String visitAppointmentId) {
        this.visitAppointmentId = visitAppointmentId;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    // Getters and Setters
}
