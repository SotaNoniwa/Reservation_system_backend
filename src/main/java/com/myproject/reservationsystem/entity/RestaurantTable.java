package com.myproject.reservationsystem.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`table`")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "table_number")
    private int tableNumber;

    @Column(name = "capacity")
    private int capacity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailableTimeSlot> availableTimeSlots;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "table",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Reservation> reservations;

    public RestaurantTable() {
    }

    public RestaurantTable(int tableNumber, int capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<AvailableTimeSlot> getAvailableTimeSlots() {
        return availableTimeSlots;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addAvailableTimeSlot(AvailableTimeSlot availableTimeSlot) {
        if (availableTimeSlots == null) {
            availableTimeSlots = new ArrayList<>();
        }
        availableTimeSlots.add(availableTimeSlot);
        availableTimeSlot.setTable(this);
    }

    public void removeAvailableTimeSlot(AvailableTimeSlot availableTimeSlot) {
        availableTimeSlots.remove(availableTimeSlot);
        availableTimeSlot.setTable(null);
    }

    public void addReservation(Reservation reservation) {
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
        reservation.setTable(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setTable(this);
    }

    @Override
    public String toString() {
        List<Integer> reservationIdList = new ArrayList<>();
        for (Reservation reservation : reservations) {
            reservationIdList.add(reservation.getId());
        }

        List<Integer> availableTimeSlotIdList = new ArrayList<>();
        for (AvailableTimeSlot slot : availableTimeSlots) {
            availableTimeSlotIdList.add(slot.getId());
        }

        return "RestaurantTable{" +
                "id=" + id +
                ", tableNumber=" + tableNumber +
                ", capacity=" + capacity +
                ", availableTimeSlotIds=" + availableTimeSlotIdList +
                ", reservationIds=" + reservationIdList +
                '}';
    }
}
