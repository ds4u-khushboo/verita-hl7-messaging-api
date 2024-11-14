package com.example.hl7project.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inbound_message")
public class MessageEntity {

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

    @Column(name = "message_Text", length = 255)
    private String messageText;

    @Column(name = "phone_number", length = 255)
    private String phoneNumber;


    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "visit_appointment_id", length = 45)
    private String visitAppointmentId;

    @ManyToOne
    @JoinColumn(name = "visit_appointment_id", insertable = false, updatable = false)
    private Appointment appointment;

    public MessageEntity(Long patientId, String type, LocalDate now) {
    }

    public MessageEntity() {

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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getVisitAppointmentId() {
        return visitAppointmentId;
    }

    public void setVisitAppointmentId(String visitAppointmentId) {
        this.visitAppointmentId = visitAppointmentId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
// Getters and Setters
}
