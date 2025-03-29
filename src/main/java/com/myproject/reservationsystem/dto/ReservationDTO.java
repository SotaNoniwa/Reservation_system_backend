package com.myproject.reservationsystem.dto;

import java.time.LocalDateTime;

public class ReservationDTO {

    private int userId;
    private int courseId;
    private LocalDateTime dateTime; // in yyyy-mm-ddThh:mm:ss format
    private long durationMinutes;
    private int numOfCustomers;
    private String note;

    public ReservationDTO(int userId, int courseId, LocalDateTime dateTime, long durationMinutes, int numOfCustomers, String note) {
        this.userId = userId;
        this.courseId = courseId;
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.numOfCustomers = numOfCustomers;
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public int getNumOfCustomers() {
        return numOfCustomers;
    }

    public String getNote() {
        return note;
    }
}
