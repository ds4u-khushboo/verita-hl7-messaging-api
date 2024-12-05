package com.example.hl7project.repository;

import com.example.hl7project.dto.AppointmentTestMessageProjection;
import com.example.hl7project.dto.AppointmentTextMessageDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

//    public Optional<Appointment> findByStatus(AppointmentStatus status);
//    public Boolean existsByPatient(Patient patient);

    //    public Optional<Appointment> findByPlacerAppointmentId(String placerApppointmnetId);
     boolean existsByVisitAppointmentId(Long appointmentId);
    @Query(value = "SELECT ap.visitAppointmentId, ap.externalPatientId, ap.appointmentDate, ap.visitStatusCode, ap.reminderMessageStatus, " +
            "DATEDIFF(CURRENT_DATE, ap.appointmentDateUtc) AS days " +
            "FROM Appointment ap " +
            "WHERE ap.visitStatusCode = 'N/S' " +
            "AND ap.reminderMessageStatus <> 'NO_SHOW_4_WEEK' " +
            "ORDER BY ap.appointmentDate")
    List<Object[]> findNoShowAppointmentsToSendTextMessages();

    //public Optional<Appointment> findByVisitAppointmentId(Long appointmentId);

     Appointment findByVisitAppointmentId(Long appointmentId);

    List<Appointment> findByVisitStatusCode(String s);

    List<Appointment> findTop2ByOrderByCreatedAtDesc();

    List<Appointment> findByIsConfirmRequestSentTrue();

    List<Appointment> deleteByVisitAppointmentId(Long visitAppId);

    List<Appointment> findByPatientAndAppointmentDate(Patient patient, String appointmentDate);
//    public long countBy(String status);

//    @Query("SELECT a FROM Appointment a WHERE a.visitStatusCode = 'No-Show' ORDER BY a.startTime DESC")
//    List<Appointment> findNoShowAppointmentsOrderByStartTimeDesc();

//    @Query("SELECT a, m FROM Appointment a JOIN a.messages m WHERE a.status = 'No_Show' ORDER BY a.appointmentId DESC")
//    List<Object[]> findNoShowAppointmentsWithPatientDetails();

//    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.visitStatusCode = 'No_Show'")
//    long countNoShowAppointments();

    @Query(value = "SELECT visit_appointment_id, external_patient_id, cm_code, created_at, " +
            "TIMESTAMPDIFF(MINUTE, created_at, NOW()) AS minutes_elapsed, " +
            "CASE WHEN LAG(cm_code) OVER (PARTITION BY external_patient_id, DATE(created_at) " +
            "ORDER BY created_at ASC) = 'NEW' THEN 1 ELSE 0 END AS IsPreviousNew " +
            "FROM appointments WHERE cm_code IN ('NEW', 'CRS', 'CRR0', 'CRR1')", nativeQuery = true)
    List<Object[]> findAppointmentsWithConfirmationStatus();
        @Query("SELECT a FROM Appointment a WHERE a.externalPatientId = :patientId ORDER BY a.appointmentDate DESC")
        List<Appointment> findLastAppointmentsByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    @Query(value = "SELECT visit_appointment_id AS visitAppointmentId, " +
            "external_patient_id AS externalPatientId, " +
            "cm_code AS cmCode, " +
            "created_at AS createdAt, " +
            "TIMESTAMPDIFF(MINUTE, created_at, NOW()) AS minutesElapsed, " +
            "CASE WHEN LAG(cm_code) OVER (PARTITION BY external_patient_id, DATE(created_at) " +
            "ORDER BY created_at ASC) = 'NEW' THEN 1 ELSE 0 END AS isPreviousNew " +
            "FROM appointments WHERE cm_code IN ('NEW', 'CRS', 'CRR0', 'CRR1') " +
            "AND DATE(created_at) = CURDATE() AND external_patient_id=:externalPatientId",
            nativeQuery = true)
    List<Object[]> findOneAppointmentWithNewConfirmationStatus(@Param("externalPatientId") String externalPatientId);


    @Query("SELECT a FROM Appointment a WHERE a.patient.externalPatientId = :patientId ORDER BY a.appointmentTime DESC")
    Appointment findLatestByPatient(@Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.homePhone = :phoneNumber AND a.appointmentTime BETWEEN :startDate AND :endDate")
    List<Appointment> findAppointmentsByPatientAndDateRange(@Param("phoneNumber") String phoneNumber,
                                                            @Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);


    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "JOIN Providers p ON a.providers.providerID = p.providerID " +
            "WHERE a.externalPatientId = :patientId " +
            "AND p.specialty = :specialty " +
            "AND a.appointmentDate BETWEEN :startDate AND :endDate")
    boolean existsNewAppointmentWithSameSpecialty(
            @Param("patientId") String patientId,
            @Param("specialty") String specialty,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

//    public boolean existsNewAppointmentWithDifferentProvider(String externalPatientId, LocalDate startDate, LocalDate endDate, String specialty) {
        // Implement a query to check for any appointment for the patient with a different provider within the date range
//        Appointment fin(externalPatientId, startDate, endDate, specialty).isPresent();

//        Appointment findByExternalPatientIdAndaAndAppointmentDateAndpAndProvidersNot
    }


