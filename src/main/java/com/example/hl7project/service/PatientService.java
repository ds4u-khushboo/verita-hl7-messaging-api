package com.example.hl7project.service;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public void savePatientData(Map<String, String> patientData) {
        Patient patient = new Patient();
//        patient.setId(patientData.get("Patient ID"));
        patient.setExternalPatientId(patientData.get("External Patient ID"));
        patient.setExternalPatientMRN(patientData.get("External Patient MRN"));
        patient.setName(patientData.get("Patient Name"));
        patient.setDateOfBirth(patientData.get("Date of Birth"));
        patient.setSex(patientData.get("Sex"));
        patient.setRace(patientData.get("Race"));
        patient.setAddress(patientData.get("Patient Address"));
        patient.setPhoneNumber(patientData.get("Home Phone Number"));
        patient.setLanguage(patientData.get("Primary Language"));
        patient.setMaritalStatus(patientData.get("Marital Status"));
//        patient.setAppointments(appointmentList);
        System.out.println("patient data saved!!!");

        patientRepository.save(patient);
    }

    public void updatePatientData(Map<String, String> patientData) {
        String patientId = patientData.get("External Patient ID");
        Patient patient = patientRepository.findByExternalPatientId(patientId);
        if (patient == null) {
            System.out.println("Patient not found for update.");
            return;
        }
        patient.setName(patientData.get("Patient Name"));
        patient.setDateOfBirth(patientData.get("Date of Birth"));
        patient.setSex(patientData.get("Sex"));
        patient.setRace(patientData.get("Race"));
        patient.setAddress(patientData.get("Patient Address"));
        patient.setPhoneNumber(patientData.get("Home Phone Number"));
        patient.setMaritalStatus(patientData.get("Marital Status"));
        patientRepository.save(patient);

    }

}
