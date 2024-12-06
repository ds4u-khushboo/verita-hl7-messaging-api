package com.example.hl7project.service;

import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public void savePatientData(Map<String, String> patientData) {
        Patient patient = new Patient();

//        patient.setId(patientData.get("Patient ID"));
        patient.setPatientId(patientData.get("External Patient ID"));
        patient.setExternalPatientMRN(patientData.get("External Patient MRN"));
        patient.setName(patientData.get("Patient Name"));
        patient.setDateOfBirth(patientData.get("Date of Birth"));
        patient.setSex(patientData.get("Sex"));
        patient.setRace(patientData.get("Race"));
        patient.setAddress(patientData.get("Patient Address"));
        patient.setHomePhone(patientData.get("Home Phone Number"));
        patient.setLanguage(patientData.get("Primary Language"));
        patient.setMaritalStatus(patientData.get("Marital Status"));
        patient.setFirstName(patientData.get("firstName"));
        patient.setLastName(patientData.get("lastName"));

//        patient.setAppointments(appointmentList);
        System.out.println("patient data saved!!!");

        patientRepository.save(patient);
    }

    public void updatePatientData(Map<String, String> patientData) {
        String patientId = patientData.get("External Patient ID");
        Patient patient = patientRepository.findByPatientId(patientId);
        if (patient == null) {
            System.out.println("Patient not found for update.");
            return;
        }
        patient.setName(patientData.get("Patient Name"));
        patient.setDateOfBirth(patientData.get("Date of Birth"));
        patient.setSex(patientData.get("Sex"));
        patient.setRace(patientData.get("Race"));
        patient.setAddress(patientData.get("Patient Address"));
        patient.setHomePhone(patientData.get("Home Phone Number"));
        patient.setMaritalStatus(patientData.get("Marital Status"));
        patientRepository.save(patient);

    }

    // check duplicate patient   MRN  firstName  LastNAme  DateOfBirth
}
