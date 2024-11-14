package com.example.hl7project.dto;

import com.example.hl7project.model.AppointmentStatus;
import java.time.LocalDateTime;

import java.util.List;

public class NoShowReportDTO {
    private List<NoShowAppointmentDTO> noShowAppointments;
    private long noShowCount;

    // Constructor
    public NoShowReportDTO(List<NoShowAppointmentDTO> noShowAppointments, long noShowCount) {
        this.noShowAppointments = noShowAppointments;
        this.noShowCount = noShowCount;
    }

    // Getters and Setters
    public List<NoShowAppointmentDTO> getNoShowAppointments() {
        return noShowAppointments;
    }

    public void setNoShowAppointments(List<NoShowAppointmentDTO> noShowAppointments) {
        this.noShowAppointments = noShowAppointments;
    }

    public long getNoShowCount() {
        return noShowCount;
    }

    public void setNoShowCount(long noShowCount) {
        this.noShowCount = noShowCount;
    }

    public static class NoShowAppointmentDTO {
        private Long appointmentId;
        private String startTime;
        private String location;
        private AppointmentStatus status;
        private String patientName;
        private String patientPhNumber;

        // Constructor
        public NoShowAppointmentDTO(Long appointmentId, String startTime, String location, AppointmentStatus status) {
            this.appointmentId = appointmentId;
            this.startTime = startTime;
            this.location = location;
            this.status = status;
//            this.patientName = patientName;
//            this.patientPhNumber = patientPhNumber;
        }

        // Getters and Setters
        public Long getAppointmentId() {
            return appointmentId;
        }

        public void setAppointmentId(Long appointmentId) {
            this.appointmentId = appointmentId;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public AppointmentStatus getStatus() {
            return status;
        }

        public void setStatus(AppointmentStatus status) {
            this.status = status;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getPatientPhNumber() {
            return patientPhNumber;
        }

        public void setPatientPhNumber(String patientPhNumber) {
            this.patientPhNumber = patientPhNumber;
        }
    }
}
