package com.example.hl7project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SlotDTO {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean isBooked;

    public SlotDTO() {

    }
}
