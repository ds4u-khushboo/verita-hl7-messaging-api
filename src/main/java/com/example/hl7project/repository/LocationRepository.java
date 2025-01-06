package com.example.hl7project.repository;

import com.example.hl7project.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByLocationId(String locationId);

    @Query("SELECT p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, a.visitAppointmentId, a.appointmentReason, " +
            "pr.providerName, pr.specialty, l.locationName, " +
            "COUNT(a.visitAppointmentId) AS appointmentCount, " +
            "SUM(CASE WHEN a.isConfirmRequestSent = true THEN 1 ELSE 0 END) AS messagesTriggered " +
            "FROM Patient p " +
            "JOIN Appointment a ON p.patientId = a.patientId " +
            "JOIN Provider pr ON pr.providerId = a.provider " +
            "JOIN Location l ON l.locationId = pr.location " +
            "WHERE l.locationName = :location " +
            "AND (a.visitStatusCode = 'PEN' OR a.visitStatusCode is null ) " +
            "AND DATE(a.appointmentDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, " +
            "a.visitAppointmentId, a.appointmentReason, pr.providerName, pr.specialty, l.locationName")
    List<Object[]> findAppointmentsByLocation(
            @Param("location") String location,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, a.visitAppointmentId, a.appointmentReason, " +
            "pr.providerName, pr.specialty, l.locationName, " +
            "COUNT(a.visitAppointmentId) AS appointmentCount, " +
            "SUM(CASE WHEN a.isConfirmRequestSent = true THEN 1 ELSE 0 END) AS messagesTriggered " +
            "FROM Patient p " +
            "JOIN Appointment a ON p.patientId = a.patientId " +
            "JOIN Provider pr ON pr.providerId = a.provider " +
            "JOIN Location l ON l.locationId = pr.location " +
            "WHERE l.locationName = :location " +
            "AND (a.visitStatusCode = 'N/S' OR a.visitStatusCode is null ) " +
            "AND DATE(a.appointmentDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY p.patientId, p.name, p.address, p.dateOfBirth, p.sex, a.visitStatusCode, a.appointmentDate, " +
            "a.visitAppointmentId, a.appointmentReason, pr.providerName, pr.specialty, l.locationName")
    List<Object[]> findNoShowAppointmentsByLocation(
            @Param("location") String location,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
