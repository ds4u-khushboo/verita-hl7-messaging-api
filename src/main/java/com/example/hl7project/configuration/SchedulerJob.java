package com.example.hl7project.configuration;

import com.example.hl7project.service.SIUInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Configuration
@EnableScheduling
public class SchedulerJob {

    @Autowired
    private SIUInboundService appointmentService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldMessages() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Timestamp timestamp = Timestamp.valueOf(thirtyDaysAgo);
        appointmentService.deleteMessage(LocalDate.from(thirtyDaysAgo));
    }
}
