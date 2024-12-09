package com.example.hl7project.repository;

import com.example.hl7project.model.Appointment;
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

    boolean existsByVisitAppointmentId(Long appointmentId);

    @Query(value = "SELECT ap.visitAppointmentId, ap.patientId, ap.appointmentDate, ap.visitStatusCode, ap.reminderMessageStatus, " +
            "DATEDIFF(CURRENT_DATE, ap.appointmentDate) AS days " +
            "FROM Appointment ap " +
            "WHERE ap.visitStatusCode = 'N/S' " +
            "AND ap.reminderMessageStatus <> 'NO_SHOW_4_WEEK' " +
            "ORDER BY ap.appointmentDate")
    List<Object[]> findNoShowAppointmentsToSendTextMessages();

    @Query(value = "SELECT ap.*, " +
            "CASE " +
            "WHEN FLOOR(TIME_TO_SEC(TIMEDIFF(TIMESTAMP(ap.appointmentDate), ap.appointment_date)) / 60) < 180 " +
            "THEN 1 " +
            "ELSE 0 " +
            "END AS timeDiffLessThan3Hours " +
            "FROM appointments ap " +
            "WHERE ap.patient_id = :patientId " +
            "AND ap.is_confirm_request_replied = 0 " +  "AND ap.visit_status_code = 'PEN' " +
            "AND FLOOR(TIME_TO_SEC(TIMEDIFF(TIMESTAMP(ap.appointmentDate), ap.appointment_date)) / 60) < 180",
            nativeQuery = true)
    List<Object[]> findAppointmentsWithTimeDiff(@Param("patientId") String patientId);

    Appointment findByVisitAppointmentId(Long appointmentId);

    List<Appointment> deleteByVisitAppointmentId(Long visitAppId);
    @Query(value = "SELECT visit_appointment_id, patient_id, cm_code, created_at, " +
            "TIMESTAMPDIFF(MINUTE, created_at, NOW()) AS minutes_elapsed, " +
            "CASE WHEN LAG(cm_code) OVER (PARTITION BY patient_id, DATE(created_at) " +
            "ORDER BY created_at ASC) = 'NEW' THEN 1 ELSE 0 END AS IsPreviousNew " +
            "FROM appointments WHERE cm_code IN ('NEW', 'CRS', 'CRR0', 'CRR1')", nativeQuery = true)
    List<Object[]> findAppointmentsWithConfirmationStatus();


    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId ORDER BY a.appointmentDate DESC")
    List<Appointment> findLastNoShowAppointmentByPatientId(@Param("patientId") Long patientId);

    @Query(value = "SELECT visit_appointment_id AS visitAppointmentId, " +
            "patient_id AS patientId, " +
            "cm_code AS cmCode, " +
            "created_at AS createdAt, " +
            "TIMESTAMPDIFF(MINUTE, created_at, NOW()) AS minutesElapsed, " +
            "CASE WHEN LAG(cm_code) OVER (PARTITION BY patient_id, DATE(created_at) " +
            "ORDER BY created_at ASC) = 'NEW' THEN 1 ELSE 0 END AS isPreviousNew " +
            "FROM appointments WHERE cm_code IN ('NEW', 'CRS', 'CRR0', 'CRR1') " +
            "AND DATE(created_at) = CURDATE() AND patient_id=:patientId",
            nativeQuery = true)
    List<Object[]> findOneAppointmentWithNewConfirmationStatus(@Param("patientId") String patientId);


    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId ORDER BY a.appointmentDate DESC")
    Appointment findLatestByPatient(@Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a WHERE  a.appointmentDate BETWEEN :startDate AND :endDate")
    List<Appointment> findAppointmentsByPatientAndDateRange(@Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);

    @Query(value = """
                SELECT  a.visit_appointment_id AS appointmentId,a.patient_id AS patientId,  a.appointment_date AS appointmentDate,  a.provider_id AS providerId,  p.specialty AS specialty, 
                    a.visit_status_code AS visitStatusCode,
                    a.reminder_message_status AS reminderMessageStatus,
                    DATEDIFF(CURDATE(), a.appointment_date) AS days FROM appointments a   LEFT JOIN providers p ON p.provider_id = a.provider_id  WHERE a.patient_id = :patientId 
                AND p.specialty = :specialty AND a.visit_status_code = 'N/S'  AND DATEDIFF(CURDATE(), a.appointment_date) < 29 
                AND a.reminder_message_status IN ('NONE', 'NO_SHOW', 'NO_SHOW_2_WEEK')
            """, nativeQuery = true)
    List<Object[]> findAppointmentsByPatientAndSpecialty(
            @Param("patientId") String patientId,
            @Param("specialty") String specialty
    );
}


