package com.example.hl7project.repository;

import com.example.hl7project.model.Appointment;
import com.example.hl7project.model.MessageEntity;
import com.example.hl7project.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageEntityRepo extends JpaRepository<MessageEntity,Long> {

//    public boolean existsByAppointment(Appointment appointmentId);

//    public MessageEntity findByAppointment(Appointment appointment);
//    public MessageEntity findByPatient(Patient patientName);

//    public List<MessageEntity> findByPhNumber(String phNumber);

    public List<MessageEntity> findMessageEntityByAppointment_StartTime(String start);

    public List<MessageEntity> deleteAllBySentAt(LocalDateTime date);

    public List<MessageEntity> findByMessageType(String type);

    List<MessageEntity> findByCreatedAtBetween(Timestamp startTimestamp, Timestamp endTimestamp);

    @Query("SELECT COUNT(me) FROM MessageEntity me WHERE me.messageType = :messageType")
    long countByMessageType(String messageType);

    List<MessageEntity> deleteByCreatedAtBefore(LocalDate cutoffDate);

    @Query(value = "SELECT m.messageType AS messageType, COUNT(*) AS messageCount FROM MessageEntity m WHERE m.messageType IN ('SIU_S12', 'SIU_S14', 'SIU_S26') GROUP BY m.messageType", nativeQuery = true)
    List<Object[]> countMessagesByType();

    LocalDate findByCreatedAt(Timestamp date);
}
