package com.example.hl7project.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {

//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_seq")
//    @SequenceGenerator(name = "patient_seq", sequenceName = "patients_seq", allocationSize = 1)
//    private Long id;

    @Id
    @Column(name = "external_patient_id", unique = true, nullable = false)
    private String externalPatientId;

    @Column(name = "external_patient_mrn")
    private String externalPatientMRN;

    @Column(name = "external_id", length = 50)
    private String externalId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;
    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "sex", length = 255)
    private String sex;

    @Column(name = "race", length = 255)
    private String race;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "home_phone", length = 15)
    private String homePhone;

    @Column(name = "additional_phone", length = 15)
    private String additionalPhone;

    @Column(name = "primary_language", length = 255)
    private String primaryLanguage;

    @Column(name = "marital_status", length = 5)
    private String maritalStatus;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "ssn", length = 30)
    private String ssn;

    @Column(name = "ethnicity", length = 50)
    private String ethnicity;

    @Column(name = "default_location", length = 50)
    private String defaultLocation;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "language", length = 255)
    private String language;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @OneToMany(mappedBy = "patient",  fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Appointment> appointments;

    public Patient(String firstName, String lastName, String dateOfBirth) {
    }

    public Patient() {

    }
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(String defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getExternalPatientId() {
        return externalPatientId;
    }

    public void setExternalPatientId(String externalPatientId) {
        this.externalPatientId = externalPatientId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public String getExternalPatientMRN() {
        return externalPatientMRN;
    }

    public void setExternalPatientMRN(String externalPatientMRN) {
        this.externalPatientMRN = externalPatientMRN;
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
// Getters and Setters
}
