package com.example.hl7project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "providers")
public class Providers {

    @Id
    @Column(name = "provider_id", length = 50)
    private String providerID;  // Unique provider ID

    @Column(name = "provider_code", length = 50)
    private String providerCode;  // Unique provider ID

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;  // First name of the provider

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;  // Last name of the provider

    @Column(name = "middle_name", length = 50)
    private String middleName;  // Middle name (optional)

    @Column(name = "specialty", length = 100)
    private String specialty;  // Specialty (e.g., Cardiology)

    @Column(name = "npi_number", length = 50)
    private String npiNumber;  // National Provider Identifier (NPI)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;  // Timestamp when record was created

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // Timestamp when record was last updated

    @OneToMany(mappedBy = "providers")
    private List<Appointment> appointmentList;
    // Getters and setters

    public String getProviderCode() {
        return providerCode;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getNpiNumber() {
        return npiNumber;
    }

    public void setNpiNumber(String npiNumber) {
        this.npiNumber = npiNumber;
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
}

