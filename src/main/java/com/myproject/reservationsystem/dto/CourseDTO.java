package com.myproject.reservationsystem.dto;

public class CourseDTO {

    private int id;
    private String name;
    private int price;

    public CourseDTO(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
