package com.example.hl7project.repository;

import com.example.hl7project.dto.AppointmentTextMessageDTO;
import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

//    public Optional<Appointment> findByStatus(AppointmentStatus status);
//    public Boolean existsByPatient(Patient patient);

    //    public Optional<Appointment> findByPlacerAppointmentId(String placerApppointmnetId);
    public boolean existsByVisitAppointmentId(String appointmentId);


    @Query(value = "SELECT ap.visit_appointment_id, " +
            "ap.appointment_date, " +
            "ap.visit_status_code, " +
            "mt.id AS text_message_id, " +
            "mt.type_code, " +
            "mt.created_at, " +
            "DATEDIFF(NOW(), ap.appointment_date) AS days " +
            "FROM messaging_app.appointments ap " +
            "LEFT JOIN (" +
            "  SELECT id, " +
            "         visit_appointment_id, " +
            "         type_code, " +
            "         created_at, " +
            "         ROW_NUMBER() OVER (PARTITION BY visit_appointment_id ORDER BY created_at DESC) AS rn " +
            "  FROM messaging_app.text_messages " +
            ") mt ON ap.visit_appointment_id = mt.visit_appointment_id AND mt.rn = 1 " +
            "WHERE ap.visit_status_code = 'N/S' " +
            "AND (mt.id IS NULL OR (mt.type_code IN ('NS', 'NSR1') AND DATEDIFF(NOW(), ap.appointment_date) > 14)) " +
            "ORDER BY mt.type_code, mt.created_at", nativeQuery = true)
    public List<AppointmentTextMessageDTO> findAppointmentsWithoutRecentTextMessages();

    public Appointment findByVisitAppointmentId(String appointmentId);

    List<Appointment> findByVisitStatusCode(String s);

    List<Appointment> findTop2ByOrderByCreatedAtDesc();

    List<Appointment> findByIsConfirmRequestSentTrue();

    List<Appointment> deleteByVisitAppointmentId(String visitAppId);

    List<Appointment> findByPatientAndAppointmentDate(Patient patient, String appointmentDate);
//    public long countBy(String status);

//    @Query("SELECT a FROM Appointment a WHERE a.visitStatusCode = 'No-Show' ORDER BY a.startTime DESC")
//    List<Appointment> findNoShowAppointmentsOrderByStartTimeDesc();

//    @Query("SELECT a, m FROM Appointment a JOIN a.messages m WHERE a.status = 'No_Show' ORDER BY a.appointmentId DESC")
//    List<Object[]> findNoShowAppointmentsWithPatientDetails();

//    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.visitStatusCode = 'No_Show'")
//    long countNoShowAppointments();


}
