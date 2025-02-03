package com.myproject.reservationsystem.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "`name`")
    private String name;

    @Column(name = "price")
    private int price;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private List<Reservation> reservations;

    public Course() {
    }

    public Course(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void add(Reservation reservation) {
        if (reservations.isEmpty()) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
    }

    @Override
    public String toString() {
        List<Integer> reservationIdList = new ArrayList<>();
        for (Reservation reservation : reservations) {
            reservationIdList.add(reservation.getId());
        }
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", reservationIds=" + reservationIdList +
                '}';
    }
}
