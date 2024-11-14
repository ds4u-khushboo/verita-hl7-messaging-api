package com.example.hl7project.repository;

import com.example.hl7project.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient,Long> {

//    public Patient findById(String patientId);

    public List<Patient> findByName(String patientName);

    public Patient findByExternalPatientId(String patientId);

    public List<Patient> findByPhoneNumber(String patientPhone);

    public boolean existsByName(String patientName);

}
