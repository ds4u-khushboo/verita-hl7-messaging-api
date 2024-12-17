package com.example.hl7project.repository;

import com.example.hl7project.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

//    public Patient findById(String patientId);

    public List<Patient> findByName(String patientName);

    @Query("SELECT p FROM Patient p WHERE p.firstName = :firstName AND p.lastName = :lastName AND p.dateOfBirth = :dateOfBirth ORDER BY p.createdAt DESC")
    Optional<Patient> findPatientByDetails(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("dateOfBirth") String dateOfBirth
    );
    Optional<Patient> findByExternalPatientMRNAndfAndFirstNameAndlAndLastNameAndDateOfBirth(String mrn, String firstName, String lastName, String dob);

    Patient findByPatientId(String patientId);

    Patient findByExternalPatientMRN(String patientMRN);

    List<Patient> findByHomePhone(String patientPhone);



}
