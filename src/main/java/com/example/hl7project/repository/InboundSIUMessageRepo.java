package com.example.hl7project.repository;

import com.example.hl7project.model.InboundHL7Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InboundSIUMessageRepo extends JpaRepository<InboundHL7Message,Long> {

//    public boolean existsByAppointment(Appointment appointmentId);

//    public InboundHL7Message findByAppointment(Appointment appointment);
//    public InboundHL7Message findByPatient(Patient patientName);

//    public List<InboundHL7Message> findByPhNumber(String phNumber);

    public List<InboundHL7Message> findInboundHL7MessageByCreatedAt(LocalDate start);

    public List<InboundHL7Message> deleteAllByCreatedAt(LocalDateTime date);

    public List<InboundHL7Message> findByMessageType(String type);

    List<InboundHL7Message> findByCreatedAtBetween(Timestamp startTimestamp, Timestamp endTimestamp);

    @Query("SELECT COUNT(me) FROM InboundHL7Message me WHERE me.messageType = :messageType")
    long countByMessageType(String messageType);

    List<InboundHL7Message> deleteByCreatedAtBefore(LocalDate cutoffDate);

    @Query(value = "SELECT m.messageType AS messageType, COUNT(*) AS messageCount FROM MessageEntity m WHERE m.messageType IN ('SIU_S12', 'SIU_S14', 'SIU_S26') GROUP BY m.messageType", nativeQuery = true)
    List<Object[]> countMessagesByType();

}
