package com.myproject.reservationsystem.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// TODO: indexing on column "time"
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "number_of_people")
    private int numOfPeople;

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reservation_table",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private List<RestaurantTable> tables;

    public Reservation() {
    }

    public Reservation(LocalDateTime startTime, LocalDateTime endTime, int numOfPeople, String note, User user, Course course, List<RestaurantTable> tables) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.numOfPeople = numOfPeople;
        this.note = note;
        this.user = user;
        this.course = course;
        this.tables = tables;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<RestaurantTable> getTables() {
        return tables;
    }

    public void setTables(List<RestaurantTable> tables) {
        this.tables = tables;
    }

    public void addTables(RestaurantTable table) {
        if (tables == null) {
            tables = new ArrayList<>();
        }
        tables.add(table);

        table.getReservations().add(this);
    }

    @Override
    public String toString() {
        List<Integer> tableIdList = new ArrayList<>();
        if (!tables.isEmpty()) {
            for (RestaurantTable table : tables) {
                tableIdList.add(table.getId());
            }
        }

        return "Reservation{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", numOfPeople=" + numOfPeople +
                ", note='" + note + '\'' +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", course=" + (course != null ? course.getId() : "null") +
                ", tableIds=" + tableIdList +
                '}';
    }
}
