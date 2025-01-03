package com.example.hl7project.dto;

import com.example.hl7project.model.Appointment;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class BookingInfoDTO {

    @NotNull(message = "Appointment type is required")
    @Size(min = 1, max = 10, message = "Appointment type must be between 1 and 10 characters")
    private String appointmentType;

    private ResourceDTO resource;

    @NotNull(message = "Provider information is required")
    private ProviderDTO provider;

    @NotNull(message = "Patient information is required")
    private PatientDTO patient;

    @NotNull(message = "Appointment time is required")
    private AppointmentDTO appointment;

    @Setter
    @Getter
    public static class ResourceDTO {

        private String id;
        private String name;
        private Boolean value;

    }

    @Getter
    @Setter
    public static class ProviderDTO {

        @NotNull(message = "Provider ID is required")
        private String id;

        @NotNull(message = "Provider name is required")
        private String name;

    }

    @Getter
    @Setter

    public static class PatientDTO {

        @NotNull(message = "Patient ID is required")
        private String id;

        @NotNull(message = "Patient name is required")
        private String name;

        @NotNull(message = "Date of Birth is required")
        @Pattern(regexp = "^\\d{8}$", message = "Date of Birth must be in the format YYYYMMDD")
        private String dob;  // Date of Birth in YYYYMMDD format

        private String gender;
        private String race;
        private String address;
        private String phone;
        private String ethnicity;
    }

    @Getter
    @Setter
    public static class AppointmentDTO {

        @NotNull(message = "Appointment start time is required")
        @Pattern(regexp = "^\\d{14}$", message = "Appointment start time must be in the format YYYYMMDDHHMMSS")
        private String startTime;  // Appointment start time (YYYYMMDDHHMMSS)

        @NotNull(message = "Appointment end time is required")
        @Pattern(regexp = "^\\d{14}$", message = "Appointment end time must be in the format YYYYMMDDHHMMSS")
        private String endTime;    // Appointment end time (YYYYMMDDHHMMSS)


        public AppointmentDTO(Appointment appointment) {
        }
    }
}
