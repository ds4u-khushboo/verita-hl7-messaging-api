package com.example.hl7project;

import com.example.hl7project.dto.AppointmentTextMessageDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import com.example.hl7project.model.TextMessage;
import com.example.hl7project.repository.AppointmentRepository;
import com.example.hl7project.repository.PatientRepository;
import com.example.hl7project.service.NoShowService;
import com.example.hl7project.service.NoShowServiceImpl;
import com.example.hl7project.service.SIUInboundService;
import com.example.hl7project.service.SchedulerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@SpringBootTest
public class SchedulerServiceTest {

    @MockBean
    private SIUInboundService siuInboundService;  // Mock SIUInboundService

    @MockBean
    private NoShowServiceImpl noShowServiceImpl;  // Mock NoShowService

    @MockBean
    private PatientRepository  patientRepository;  // Mock NoShowService

    @MockBean
    private AppointmentRepository appointmentRepository;  // Mock AppointmentRepository

    @Autowired
    private SchedulerService schedulerService;  // Autowire SchedulerService to test the @Scheduled task

    @Test
    public void testNoShowSchedulerTask() {
        // Given: Prepare the mock behavior
        AppointmentTextMessageDTO mockAppointment = new AppointmentTextMessageDTO();
        mockAppointment.setAppointmentDate(LocalDate.from(LocalDateTime.now()));
        mockAppointment.setExternalPatientId("12345");
        mockAppointment.setVisitAppointmentId(123L);
        mockAppointment.setTypeCode("NS");

        // Assuming sendNoShowAppointmentMessages would be called
        when(siuInboundService.sendNoShowAppointmentMessages()).thenReturn("success");

        // When: Trigger the scheduled task manually (i.e., simulating the cron job)
        schedulerService.noshowScheudler();

        // Then: Verify that the sendNoShowAppointmentMessages method is called
        verify(siuInboundService, times(1)).sendNoShowAppointmentMessages();
    }

    @Test
    public void testNoShowMessageSent() {
        // Given: Mock Appointment
        Appointment appointment = mock(Appointment.class);
        when(appointment.getSmsSentStatus()).thenReturn(null);  // Ensure TypeCode is null
        when(appointment.getVisitAppointmentId()).thenReturn(123L); // Mock Appointment ID
        when(appointmentRepository.findByVisitAppointmentId(123L)).thenReturn(appointment);

        // Create a mock patient
        Patient patient = mock(Patient.class);
        when(patientRepository.findByExternalPatientId(anyString())).thenReturn(patient);

        // Assuming sendNoShowAppointmentMessages would be called when needed
        when(appointmentRepository.existsByVisitAppointmentId(123L)).thenReturn(false);

        siuInboundService.sendNoShowAppointmentMessages();
        verify(noShowServiceImpl, times(1)).sendNoShowMessage(anyString(), anyString());

        // When: Call the service directly or trigger the method

        // Then: Verify that sendNoShowMessage is called once
    }

}
