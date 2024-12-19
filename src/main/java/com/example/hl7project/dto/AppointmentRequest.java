package com.example.hl7project.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentRequest {

    private String sendingApplication;
    private String sendingFacility;
    private String receivingApplication;
    private String receivingFacility;
    private String dateTimeOfMessage;
    private String messageType;
    private String messageControlId;
    private String processingId;
    private String versionId;
    private Resource resource;
    private boolean resourceRequired;
    private Location location;

    private String visitAppointmentIdVendor;
    private String visitAppointmentIdECW;
    private String appointmentReason;
    private String appointmentVisitType;
    private String duration;
    private String durationUnits;
    private String appointmentTimingQuantity;
    private String startDateTime;
    private String endDateTime;  // Use LocalDateTime for consistency
    private String resourceName;
    private String encounterNotes;
    private String visitStatusCode;
    private Provider provider;
    private Patient patient;
    private Visit visit;
    private Insurance insurance;
    private Guarantor guarantor;

    @Getter
    @Setter
    public static class Resource {
        private String resourceId;
        private String lastName;
        private String resourceType;
        private String firstName;
        private LocalDateTime startDateTime; // Change to LocalDateTime
        private String duration;
        private String durationUnits;
    }

    @Getter
    @Setter
    public static class Location {
        private String locationId;
        private String locationName;
    }

    @Getter
    @Setter
    public static class Provider {
        private String providerId;
        private String providerName;
        private String firstName;
        private String lastName;
        private LocalDateTime startDateTime;
        private String duration;
        private String durationUnits;
        private String statusCode;
    }

    @Getter
    @Setter
    public  class Patient {
        private String lastName;
        private String firstName;
        private String middleName;
        private String dob;
        private String sex;
        private String race;
        private Address address;
        private String phone;
        private String businessPhone;
        private String cellPhone;
        private String language;
        private String maritalStatus;
        private String ethnicity;
        private String statementFlag;
        private String statementDate;
        private String deathDate;
        private String deathIndicator;
        private String ssn;
    }

    @Getter
    @Setter
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zip;
    }
    @Getter
    @Setter
    public static class Visit {
        private String patientClass;
        private AssignedLocation assignedLocation;
        private Doctor attendingDoctor;
        private String visitNumber;
        private LocalDateTime admitDate;
    }
    @Getter
    @Setter
    public static class AssignedLocation {
        private String pointOfCare;
        private String room;
        private String bed;
    }
    @Getter
    @Setter
    public static class Doctor {
        private String id;
        private String lastName;
        private String firstName;
    }
    @Getter
    @Setter
    public static class Insurance {
        private String planId;
        private String companyName;
        private Address address;
        private String policyNumber;
    }

    private String id;
    private String resourceId;
    private String scheduleId;
    private String eventReasonCode;
    private String appointmentType;
    private LocalDateTime dateTimeOfTheEvent;  // Use LocalDateTime

    private String locationId;
    private String locationName;
    private String locationType;
    private String locationAddress;

    private String participantId;
    private String participantRole;
    private String participantName;
    private String participantContact;

    @Getter
    @Setter
    public class Guarantor {
        private String guarantorId;
        private String guarantorName;
        private String guarantorRelationship;
        private String guarantorAddress;
        private String guarantorPhone;
        private String guarantorDob;
        private String guarantorSex;
        private String guarantorType;
        private String guarantorSSN;
        private String guarantorEmploymentStatus;
    }
}
