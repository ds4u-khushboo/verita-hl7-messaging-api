package com.example.hl7project.model;

import com.example.hl7project.utility.ConfirmationMessageStatus;
import com.example.hl7project.utility.ReminderMessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "visit_appointment_id")
    private Long visitAppointmentId;

    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Column(name = "appointment_date_str")
    private String appointmentDateStr;

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

    @Column(name = "is_confirm_request_sent")
    private Boolean isConfirmRequestSent;

    @Column(name = "is_confirm_request_replied")
    private Boolean isConfirmRequestReplied;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "provider_id")
    private String provider;

    @Column(name = "location_id")
    private String location;

    @Column(name = "resource_id")
    private String resourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_message_status", columnDefinition = "ENUM('NONE') DEFAULT 'NONE'")
    private ReminderMessageStatus reminderMessageStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmation_message_status")
    private ConfirmationMessageStatus confirmationMessageStatus;

    public Appointment() {

    }
}

