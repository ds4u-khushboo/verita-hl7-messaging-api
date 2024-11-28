package com.example.hl7project.dto;

// Root class representing the entire JSON structure
public class AppointmentRequest {
    public String sendingApplication;
    public String sendingFacility;
    public String receivingApplication;
    public String receivingFacility;
    public String dateTimeOfMessage;
    public String messageType;
    public String messageControlId;
    public String processingId;
    public String versionId;
    public Resource resource;
    public Location location;
    public Provider provider;
    public Patient patient;
    public Visit visit;
    public Insurance insurance;

    public String getSendingApplication() {
        return sendingApplication;
    }

    public void setSendingApplication(String sendingApplication) {
        this.sendingApplication = sendingApplication;
    }

    public String getSendingFacility() {
        return sendingFacility;
    }

    public void setSendingFacility(String sendingFacility) {
        this.sendingFacility = sendingFacility;
    }

    public String getReceivingApplication() {
        return receivingApplication;
    }

    public void setReceivingApplication(String receivingApplication) {
        this.receivingApplication = receivingApplication;
    }

    public String getReceivingFacility() {
        return receivingFacility;
    }

    public void setReceivingFacility(String receivingFacility) {
        this.receivingFacility = receivingFacility;
    }

    public String getDateTimeOfMessage() {
        return dateTimeOfMessage;
    }

    public void setDateTimeOfMessage(String dateTimeOfMessage) {
        this.dateTimeOfMessage = dateTimeOfMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageControlId() {
        return messageControlId;
    }

    public void setMessageControlId(String messageControlId) {
        this.messageControlId = messageControlId;
    }

    public String getProcessingId() {
        return processingId;
    }

    public void setProcessingId(String processingId) {
        this.processingId = processingId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }


// Getters and Setters
    // (Generate these using your IDE or manually)

    // Resource class
  public static   class Resource {
        public String resourceId;
        public String lastName;
        public String firstName;
        public String startDateTime;
        public String duration;
        public String durationUnits;

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(String startDateTime) {
            this.startDateTime = startDateTime;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDurationUnits() {
            return durationUnits;
        }

        public void setDurationUnits(String durationUnits) {
            this.durationUnits = durationUnits;
        }


// Getters and Setters
    }

    // Location class
  public static class Location {
        public String locationId;
        public String locationName;

        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }
// Getters and Setters
    }

    // Provider class
    public static class Provider {
        public String providerId;
        public String lastName;
        public String firstName;
        public String startDateTime;
        public String duration;
        public String durationUnits;
        public String statusCode;

        public String getProviderId() {
            return providerId;
        }

        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(String startDateTime) {
            this.startDateTime = startDateTime;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDurationUnits() {
            return durationUnits;
        }

        public void setDurationUnits(String durationUnits) {
            this.durationUnits = durationUnits;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }
// Getters and Setters
    }

    // Patient class
   public static class Patient {
        public String mrnNo;
        public String lastName;
        public String firstName;
        public String middleName;
        public String dob;
        public String sex;
        public Address address;
        public String phone;

        public String getMrnNo() {
            return mrnNo;
        }

        public void setMrnNo(String mrnNo) {
            this.mrnNo = mrnNo;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
// Getters and Setters
    }

    // Address class (used in both Patient and Insurance)
    public static class Address {
        public String street;
        public String city;
        public String state;
        public String zip;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }
// Getters and Setters
    }

    // Visit class
    public static class Visit {
        public String patientClass;
        public AssignedLocation assignedLocation;
        public Doctor attendingDoctor;
        public String visitNumber;
        public String admitDate;

        public String getPatientClass() {
            return patientClass;
        }

        public void setPatientClass(String patientClass) {
            this.patientClass = patientClass;
        }

        public AssignedLocation getAssignedLocation() {
            return assignedLocation;
        }

        public void setAssignedLocation(AssignedLocation assignedLocation) {
            this.assignedLocation = assignedLocation;
        }

        public Doctor getAttendingDoctor() {
            return attendingDoctor;
        }

        public void setAttendingDoctor(Doctor attendingDoctor) {
            this.attendingDoctor = attendingDoctor;
        }

        public String getVisitNumber() {
            return visitNumber;
        }

        public void setVisitNumber(String visitNumber) {
            this.visitNumber = visitNumber;
        }

        public String getAdmitDate() {
            return admitDate;
        }

        public void setAdmitDate(String admitDate) {
            this.admitDate = admitDate;
        }
// Getters and Setters
    }

    // AssignedLocation class (nested in Visit)
  public static class AssignedLocation {
        public String pointOfCare;
        public String room;
        public String bed;

        public String getPointOfCare() {
            return pointOfCare;
        }

        public void setPointOfCare(String pointOfCare) {
            this.pointOfCare = pointOfCare;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public String getBed() {
            return bed;
        }

        public void setBed(String bed) {
            this.bed = bed;
        }
// Getters and Setters
    }

    // Doctor class (nested in Visit)
 public static  class Doctor {
        public String id;
        public String lastName;
        public String firstName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
// Getters and Setters
    }

    // Insurance class
   public static class Insurance {
        public String planId;
        public String companyName;
        public Address address;
        public String policyNumber;

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getPolicyNumber() {
            return policyNumber;
        }

        public void setPolicyNumber(String policyNumber) {
            this.policyNumber = policyNumber;
        }
// Getters and Setters
    }

    private String id;

    private String resourceId;

    private String StartDateTime;
    private String scheduleId;
    private String eventReasonCode;
    private String appointmentType;
    private String dateTimeOfTheEvent;
    private String duration;
    private String durationUnits;

    // AIL (Appointment Information Location) fields
    private String locationId;
    private String locationName;
    private String locationType;
    private String locationAddress;

    // AIP (Appointment Information Participant) fields
    private String participantId;
    private String participantRole;
    private String participantName;
    private String participantContact;

    // Appointment details (e.g., Patient Information)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        StartDateTime = startDateTime;
    }

    // Getters and setters for all fields
    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getEventReasonCode() {
        return eventReasonCode;
    }

    public void setEventReasonCode(String eventReasonCode) {
        this.eventReasonCode = eventReasonCode;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getDateTimeOfTheEvent() {
        return dateTimeOfTheEvent;
    }

    public void setDateTimeOfTheEvent(String dateTimeOfTheEvent) {
        this.dateTimeOfTheEvent = dateTimeOfTheEvent;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDurationUnits() {
        return durationUnits;
    }

    public void setDurationUnits(String durationUnits) {
        this.durationUnits = durationUnits;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getParticipantRole() {
        return participantRole;
    }

    public void setParticipantRole(String participantRole) {
        this.participantRole = participantRole;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getParticipantContact() {
        return participantContact;
    }

    public void setParticipantContact(String participantContact) {
        this.participantContact = participantContact;
    }
}
