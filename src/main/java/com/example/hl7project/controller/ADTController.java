package com.example.hl7project.controller;

import com.example.hl7project.model.Patient;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.service.ADTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/hl7")
public class ADTController {

    @Autowired
    private ADTService adtService;

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping("/ADT")
    public Message getAdtMessage(@RequestBody String adtMessage) {
        return adtService.processMessage(adtMessage);
    }

    @PostMapping("/ADTA31")
    public ResponseEntity<Patient> updatePatient(  @RequestParam String mrn,
                                                   @RequestParam String firstName,
                                                   @RequestParam String lastName,
                                                   @RequestParam String dob,
                                                   @RequestBody Patient patientDetails) {
            Optional<Patient> patient = patientRepository.findByExternalPatientMRNAndfAndFirstNameAndlAndLastNameAndDateOfBirth(mrn,firstName,lastName,dob);


        if (patientOptional.isPresent()) {
            Patient patient = patientOptional.get();
            patient.setFirstName(patientDetails.getFirstName());
            patient.setLastName(patientDetails.getLastName());
            patient.setDob(patientDetails.getDob());
            patient.setEmail(patientDetails.getEmail());
            patient.setPhone(patientDetails.getPhone());

            return patientRepository.save(patient);
        } else {
            throw new RuntimeException("Patient not found with provided details");
        }
    }
    }
    @PostMapping("/adtmsg")
    public void createADTmessage() {

    }
}
