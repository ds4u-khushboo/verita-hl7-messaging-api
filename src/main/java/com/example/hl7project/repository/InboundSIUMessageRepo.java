package com.example.hl7project.repository;

import com.example.hl7project.model.InboundHL7Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InboundSIUMessageRepo extends JpaRepository<InboundHL7Message, Long> {

     List<InboundHL7Message> findInboundHL7MessageByCreatedAt(LocalDate start);

     List<InboundHL7Message> deleteAllByCreatedAt(LocalDateTime date);


    List<InboundHL7Message> findByCreatedAtBetween(Timestamp startTimestamp, Timestamp endTimestamp);

    @Query("SELECT COUNT(me) FROM InboundHL7Message me WHERE me.messageType = :messageType")
    long countByMessageType(String messageType);

    @Query(value = "SELECT * FROM inbound_siu_message m " +
            "WHERE (m.sent_at IS NULL AND m.created_at <= :currentTime) " +
            "OR (m.sent_at IS NOT NULL AND m.created_at >= DATE_ADD(m.sent_at, INTERVAL 3 MINUTE)) " +
            "ORDER BY m.created_at ASC", nativeQuery = true)
    List<InboundHL7Message> findPendingMessages(@Param("currentTime") LocalDateTime currentTime);

    List<InboundHL7Message> deleteByCreatedAtBefore(LocalDate cutoffDate);

    @Query(value = "SELECT m.messageType AS messageType, COUNT(*) AS messageCount FROM MessageEntity m WHERE m.messageType IN ('SIU_S12', 'SIU_S14', 'SIU_S26') GROUP BY m.messageType", nativeQuery = true)
    List<Object[]> countMessagesByType();
}
