package com.example.hl7project.repository;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

//    public Optional<Appointment> findByStatus(AppointmentStatus status);
//    public Boolean existsByPatient(Patient patient);

    //    public Optional<Appointment> findByPlacerAppointmentId(String placerApppointmnetId);
    public boolean existsByVisitAppointmentId(String appointmentId);

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
