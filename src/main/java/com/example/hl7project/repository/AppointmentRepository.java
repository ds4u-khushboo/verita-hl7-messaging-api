package com.example.hl7project.repository;

import com.example.hl7project.model.Appointment;
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

//    @Query(value = "SELECT ap.*, " +
//            "CASE " +
//            "WHEN FLOOR(TIME_TO_SEC(TIMEDIFF(TIMESTAMP(CURRENT_TIMESTAMP), ap.appointment_date)) / 60) < 180 " +
//            "THEN 1 " +
//            "ELSE 0 " +
//            "END AS timeDiffLessThan3Hours " +
//            "FROM appointments ap " +
//            "WHERE ap.patient_id = :patientId " +
//            "AND ap.is_confirm_request_replied = 0 " + "AND ap.visit_status_code = 'PEN' " +
//            "AND FLOOR(TIME_TO_SEC(TIMEDIFF(TIMESTAMP(CURRENT_TIMESTAMP), ap.appointment_date)) / 60) < 180",
//            nativeQuery = true)
//    List<Object[]> findAppointmentsWithTimeDiff(@Param("patientId") String patientId);

    @Query(value = "SELECT ap.*, \n" +
            "       TIMESTAMPDIFF(MINUTE, prev.appointment_date, ap.appointment_date) AS time_diff_in_minutes\n" +
            "FROM appointments ap\n" +
            "JOIN appointments prev \n" +
            "  ON ap.patient_id = prev.patient_id\n" +
            "  AND DATE(ap.appointment_date) = DATE(prev.appointment_date) \n" +
            "  AND ap.appointment_date > prev.appointment_date  \n" +
            "WHERE ap.patient_id = :patientId\n" +
            "  AND ap.is_confirm_request_replied = 0\n" +
            "  AND ap.visit_status_code = 'PEN'\n" +
            "  AND prev.is_confirm_request_replied = 0\n" +
            "  AND prev.visit_status_code = 'PEN'\n" +
            "ORDER BY ap.appointment_date DESC \n" +
            "LIMIT 1;",
            nativeQuery = true)
    List<Object[]> findAppointmentsWithTimeDiff(@Param("patientId") String patientId);

    Appointment findByVisitAppointmentId(Long appointmentId);

    void deleteByVisitAppointmentId(Long visitAppId);

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

    @Query("SELECT a FROM Appointment a WHERE a.resourceId = :resourceId AND FUNCTION('DATE', a.appointmentDate) = FUNCTION('DATE', :localDate)")
    List<Appointment> findByResourceIdAndLocalDate(@Param("resourceId") String resourceId, @Param("localDate") LocalDate localDate);

    @Query("SELECT DISTINCT a.appointmentType FROM Appointment a WHERE a.resourceId = :resourceId")
    List<String> findDistinctAppointmentTypesByResourceId(@Param("resourceId") String resourceId);

    @Query("SELECT COUNT(*) FROM Appointment a " +
            "WHERE a.visitStatusCode = 'PEN' " +
            "AND (a.patientId = :patientId OR :patientId is null) " +
            "AND (DATE(a.createdAt) BETWEEN :startDate AND :endDate OR :startDate is null or :endDate is null )")
    long countBookedAppointments(@Param("patientId") String patientId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(*) FROM Appointment a " +
            "WHERE  a.visitStatusCode = 'N/S' " +
            "AND ( a.patientId = :patientId OR :patientId is null ) " +
            "AND (DATE(a.appointmentDate) BETWEEN :startDate AND :endDate OR :startDate is null or :endDate is null)")
    long countNoShowAppointments(@Param("patientId") String patientId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);


    @Query("SELECT p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, a.visitAppointmentId, a.appointmentReason, a.isConfirmRequestSent ," +
            "COUNT(a.visitAppointmentId) AS appointmentCount, " +
            "SUM(CASE WHEN a.isConfirmRequestSent = true THEN 1 ELSE 0 END) AS messagesTriggered " +
            "FROM Patient p " +
            "JOIN Appointment a ON p.patientId = a.patientId " +
            "WHERE a.visitStatusCode = 'PEN' " +
            "AND (Date(a.createdAt) BETWEEN :startDate AND :endDate) " +
            "AND p.patientId = :patientId " +
            "GROUP BY p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, a.visitAppointmentId, a.appointmentReason, a.isConfirmRequestSent")
    List<Object[]> findBookedAppointmentsWithPatientDetails(@Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate,
                                                            @Param("patientId") String patientId);

    @Query("SELECT p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, a.visitAppointmentId, a.appointmentReason, " +
            "COUNT(a.visitAppointmentId) AS appointmentCount, " +
            "SUM(CASE WHEN a.isConfirmRequestSent = true THEN 1 ELSE 0 END) AS messagesTriggered " +
            "FROM Patient p " +
            "JOIN Appointment a ON p.patientId = a.patientId " +
            "WHERE a.visitStatusCode = 'N/S' " +
            "AND Date(a.createdAt) BETWEEN :startDate AND :endDate " +
            "AND p.patientId = :patientId " +
            "GROUP BY p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, a.visitAppointmentId, a.appointmentReason")
    List<Object[]> findNoShowAppointmentsWithPatientDetails(@Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate,
                                                            @Param("patientId") String patientId);

    @Query("SELECT p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, " +
            "a.visitAppointmentId, a.appointmentReason, pr.providerName, pr.specialty, " +
            "COUNT(a.visitAppointmentId) AS appointmentCount, " +
            "SUM(CASE WHEN a.isConfirmRequestSent = true THEN 1 ELSE 0 END) AS messagesTriggered " +
            "FROM Patient p " +
            "JOIN Appointment a ON p.patientId = a.patientId " +
            "JOIN Provider pr ON pr.providerId = a.provider " +
            "WHERE (:providerId IS NULL OR pr.providerId = :providerId) " +
            "AND (:specialtyName IS NULL OR pr.specialty = :specialtyName) " +
            "AND a.visitStatusCode = 'PEN' " +
            "AND DATE(a.appointmentDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, " +
            "a.appointmentDate, a.visitAppointmentId, a.appointmentReason, pr.providerName, pr.specialty")
    List<Object[]> findBookedAppointmentsWithProviders(
            @Param("providerId") Long providerId,
            @Param("specialtyName") String specialtyName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, " +
            "a.visitAppointmentId, a.appointmentReason, pr.providerName, pr.specialty, " +
            "COUNT(a.visitAppointmentId) AS appointmentCount, " +
            "SUM(CASE WHEN a.isConfirmRequestSent = true THEN 1 ELSE 0 END) AS messagesTriggered " +
            "FROM Patient p " +
            "JOIN Appointment a ON p.patientId = a.patientId " +
            "JOIN Provider pr ON pr.providerId = a.provider " +
            "WHERE (:providerId IS NULL OR pr.providerId = :providerId) " +
            "AND (:specialtyName IS NULL OR pr.specialty = :specialtyName) " +
            "AND a.visitStatusCode = 'N/S' " +
            "AND DATE(a.appointmentDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, " +
            "a.appointmentDate, a.visitAppointmentId, a.appointmentReason, pr.providerName, pr.specialty")
    List<Object[]> findNoShowAppointmentsWithProviders(
            @Param("providerId") Long providerId,
            @Param("specialtyName") String specialtyName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    //    @Query("SELECT COUNT(*) FROM Appointment a " +
//            "WHERE  a.visitStatusCode = 'PEN' " +
//            "AND ( a.patientId = :patientId OR :patientId is null ) " +
//            "AND (DATE(a.createdAt) BETWEEN :startDate AND :endDate OR :startDate is null or :endDate is null)")
//    long countBookedAppointments(@Param("patientId") String patientId,
//                                 @Param("startDate") LocalDate  startDate,
//                                 @Param("endDate") LocalDate  endDate);
//count booked appointments by created date
    //join patient table
    //patient summary - booked appointment no show appointments and message sent
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate")
    long countAppointmentsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.visitStatusCode = 'N/S' AND a.appointmentDate BETWEEN :startDate AND :endDate")
    long countNoShowsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Appointment a WHERE " +
            "(:startDate IS null OR a.appointmentDate >= :startDate) AND " +
            "(:endDate IS null OR a.appointmentDate <= :endDate)")
    List<Appointment> findAppointmentsByDateRangeBetween(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    //    @Query(value = "SELECT p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, " +
//            "a.appointmentDate, a.visitAppointmentId, a.appointmentReason, " +
//            "TIMESTAMPDIFF(YEAR, p.dateOfBirth, CURRENT_DATE) - " +
//            "(DATE_FORMAT(CURRENT_DATE, '%m%d') < DATE_FORMAT(p.dateOfBirth, '%m%d')) AS age " +
//            "FROM Appointment a " +
//            "JOIN Patient p ON a.patientId = p.patientId " +
//            "WHERE a.visitStatusCode = 'PEN' " +
//            "AND (:gender IS NULL OR p.sex = :gender) " +
//            "AND (:address IS NULL OR p.address LIKE :address) " +
//            "AND (:patientName IS NULL OR p.name LIKE :patientName) " +
//            "AND (:minAge IS NULL OR :maxAge IS NULL OR TIMESTAMPDIFF(YEAR, p.dateOfBirth, CURRENT_DATE) BETWEEN :minAge AND :maxAge) " +
//            "AND a.appointmentDate BETWEEN :startDate AND :endDate",
//            nativeQuery = true)
    @Query("SELECT p.patientId, p.name, p.address, DATE_FORMAT(p.dateOfBirth, '%Y-%m-%d') AS dateOfBirth, " +
            "p.sex, a.visitStatusCode, DATE_FORMAT(a.appointmentDate, '%Y-%m-%d %H:%i:%s') AS appointmentDate, " +
            "a.visitAppointmentId, a.appointmentReason, " +
            "COUNT(a.visitAppointmentId) AS appointmentCount, " +
            "SUM(CASE WHEN a.isConfirmRequestSent = true THEN 1 ELSE 0 END) AS messagesTriggered, " +
            "YEAR(CURRENT_DATE) - YEAR(date(p.dateOfBirth)) AS age " +
            "FROM Patient p " +
            "JOIN Appointment a ON p.patientId = a.patientId " +
            "WHERE a.visitStatusCode = 'PEN' " +
            "AND (:gender IS NULL OR p.sex = :gender) " +
            "AND (:address IS NULL OR TRIM(p.address) LIKE TRIM(:address)) " +
            "AND (:patientName IS NULL OR TRIM(p.name) = TRIM(:patientName)) " +
            "AND (DATE(a.appointmentDate) BETWEEN :startDate AND :endDate OR :startDate is null or :endDate is null )" +
            "AND (YEAR(CURRENT_DATE) - YEAR(date(p.dateOfBirth)) BETWEEN :minAge AND :maxAge OR :minAge is null or :maxAge is null) " +
            "GROUP BY p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, a.visitAppointmentId, a.appointmentReason")
    List<Object[]> findBookedAppointmentsWithPatientDemographics(
            @Param("gender") String gender,
            @Param("patientName") String patientName,
            @Param("address") String address,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT p.patientId, p.name, p.address, DATE_FORMAT(p.dateOfBirth, '%Y-%m-%d') AS dateOfBirth, " +
            "p.sex, a.visitStatusCode, DATE_FORMAT(a.appointmentDate, '%Y-%m-%d %H:%i:%s') AS appointmentDate, " +
            "a.visitAppointmentId, a.appointmentReason, " +
            "COUNT(a.visitAppointmentId) AS appointmentCount, " +
            "SUM(CASE WHEN a.isConfirmRequestSent = true THEN 1 ELSE 0 END) AS messagesTriggered, " +
            "YEAR(CURRENT_DATE) - YEAR(date(p.dateOfBirth)) AS age " +
            "FROM Patient p " +
            "JOIN Appointment a ON p.patientId = a.patientId " +
            "WHERE a.visitStatusCode = 'N/S' " +
            "AND (:gender IS NULL OR p.sex = :gender) " +
            "AND (:address IS NULL OR TRIM(p.address) LIKE TRIM(:address)) " +
            "AND (:patientName IS NULL OR TRIM(p.name) = TRIM(:patientName)) " +
            "AND (DATE(a.appointmentDate) BETWEEN :startDate AND :endDate OR :startDate is null or :endDate is null )" +
            "AND (YEAR(CURRENT_DATE) - YEAR(date(p.dateOfBirth)) BETWEEN :minAge AND :maxAge OR :minAge is null or :maxAge is null) " +
            "GROUP BY p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, a.visitAppointmentId, a.appointmentReason")
    List<Object[]> findNoShowAppointmentsWithPatientDemographics(
            @Param("gender") String gender,
            @Param("patientName") String patientName,
            @Param("address") String address,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}


