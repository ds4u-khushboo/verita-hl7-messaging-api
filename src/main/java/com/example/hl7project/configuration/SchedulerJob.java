package com.example.hl7project.configuration;

import com.example.hl7project.service.AppointmentService;
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
    private AppointmentService appointmentService;

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void deleteOldMessages() {
        // Example: Delete messages older than 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Timestamp timestamp = Timestamp.valueOf(thirtyDaysAgo);
        appointmentService.deleteMessage(LocalDate.from(thirtyDaysAgo));
    }
}
