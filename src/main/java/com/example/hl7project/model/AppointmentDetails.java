package com.example.hl7project.model;
public class AppointmentDetails {
    Long appointmentId;
    String startDateTime;
    String location;
    String patientName;
    String patientPhone;

    String startTime;

    public AppointmentDetails(){

    }
    public AppointmentDetails(Long appointmentId, String startDateTime, String location, String patientName, String patientPhone) {
        this.appointmentId = appointmentId;
        this.startDateTime = startDateTime;
        this.location = location;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }
}
