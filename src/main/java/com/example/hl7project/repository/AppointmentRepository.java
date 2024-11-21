package com.example.hl7project.repository;

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
     boolean existsByVisitAppointmentId(Long appointmentId);


    @Query(value = "SELECT \n" +
            "    ap.visit_appointment_id,\n" +
            "    ap.external_patient_id,\n"+
            "    ap.appointment_date,\n" +
            "    ap.visit_status_code,\n" +
            "    mt.id AS text_message_id,\n" +
            "    mt.type_code,\n" +
            "    mt.created_at,\n" +
            "    DATEDIFF(NOW(), ap.appointment_date) AS days\n" +
            "FROM messaging_app.appointments ap\n" +
            "LEFT JOIN (\n" +
            "    SELECT \n" +
            "        id,\n" +
            "        visit_appointment_id,\n" +
            "        type_code,\n" +
            "        created_at,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY visit_appointment_id ORDER BY created_at DESC) AS rn\n" +
            "    FROM messaging_app.text_messages\n" +
            ") mt ON ap.visit_appointment_id = mt.visit_appointment_id AND mt.rn = 1\n" +
            "WHERE ap.visit_status_code = 'N/S'\n" +
            "AND (\n" +
            "    mt.id IS NULL OR\n" +
            "    (mt.type_code IN ('NS', 'NSR1') AND DATEDIFF(NOW(), ap.appointment_date) > 14)\n" +
            ")\n" +
            "ORDER BY mt.type_code, mt.created_at", nativeQuery = true)
    List<Object[]> findNoShowAppointmentsToSendTextMessages();

//    public Optional<Appointment> findByVisitAppointmentId(Long appointmentId);

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


}
