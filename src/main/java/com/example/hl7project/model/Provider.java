package com.example.hl7project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "providers")
@Data
public class Provider {

    @Id
    @Column(name = "id", length = 50)
    private String Id;

    @Column(name = "provider_id", length = 50)
    private String providerId;

    @Column(name = "provider_name", nullable = false, length = 50)
    private String providerName;

    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "npi_number", length = 50)
    private String npiNumber;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    @OneToMany(mappedBy = "providers")
//    private List<Appointment> appointmentList;

}

