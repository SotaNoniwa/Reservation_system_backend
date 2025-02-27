package com.myproject.reservationsystem.dto;

import java.time.LocalDateTime;

public class AvailableTimeSlotDTO {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public AvailableTimeSlotDTO(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
