package com.example.hl7project.utility;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class Utility {

    public LocalDateTime hl7DateToDateTime(String date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

        return dateTime;
    }
    public  String formatToHL7DateTime(LocalDateTime localDateTime) {
        // Define HL7 date-time format (YYYYMMDDHHMMSS)
        DateTimeFormatter hl7Formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Format the LocalDateTime into HL7 date-time string
        return localDateTime.format(hl7Formatter);
    }
}
