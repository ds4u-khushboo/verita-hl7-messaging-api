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
}
