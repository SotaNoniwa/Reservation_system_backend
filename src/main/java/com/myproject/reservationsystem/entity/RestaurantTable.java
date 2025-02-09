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

    @Column(name = "`name`")
    private String name;

    @Column(name = "capacity")
    private int capacity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailableTimeSlot> availableTimeSlots;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tables",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Reservation> reservations;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "adjacent_table",
            joinColumns = @JoinColumn(name = "table_id"),
            inverseJoinColumns = @JoinColumn(name = "adjacent_table_id")
    )
    private List<RestaurantTable> adjacentTables;

    public RestaurantTable() {
    }

    public RestaurantTable(int capacity, String name) {
        this.capacity = capacity;
        this.name = name;
    }

    public RestaurantTable(String name, int capacity, List<RestaurantTable> adjacentTables) {
        this.name = name;
        this.capacity = capacity;
        this.adjacentTables = adjacentTables;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        reservation.getTables().add(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);

        reservation.getTables().remove(this);
    }

    public List<RestaurantTable> getAdjacentTables() {
        return adjacentTables;
    }

    public void setAdjacentTables(List<RestaurantTable> adjacentTables) {
        this.adjacentTables = adjacentTables;
    }

    public void addAdjacentTable(RestaurantTable table) {
        if (adjacentTables == null) {
            adjacentTables = new ArrayList<>();
        }
        adjacentTables.add(table);
        table.getAdjacentTables().add(this);
    }

    public void removeAdjacentTable(RestaurantTable table) {
        adjacentTables.remove(table);
        table.getAdjacentTables().remove(table);
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

        List<Integer> adjacentTableIdList = new ArrayList<>();
        for (RestaurantTable table : adjacentTables) {
            adjacentTableIdList.add(table.getId());
        }

        return "RestaurantTable{" +
                "id=" + id +
                ", location=" + name +
                ", capacity=" + capacity +
                ", availableTimeSlotIds=" + availableTimeSlotIdList +
                ", reservationIds=" + reservationIdList +
                ", adjacentTableIds=" + adjacentTableIdList +
                '}';
    }
}
