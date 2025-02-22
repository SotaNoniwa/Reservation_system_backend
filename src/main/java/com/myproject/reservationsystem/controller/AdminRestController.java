package com.myproject.reservationsystem.controller;

import com.myproject.reservationsystem.dto.AvailableTimeSlotDTO;
import com.myproject.reservationsystem.entity.AvailableTimeSlot;
import com.myproject.reservationsystem.entity.RestaurantTable;
import com.myproject.reservationsystem.service.ReservationSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private ReservationSystemService reservationSystemService;

    @Autowired
    public AdminRestController(ReservationSystemService reservationSystemService) {
        this.reservationSystemService = reservationSystemService;
    }

    @PostMapping("/available-time-slot")
    public ResponseEntity<String> saveAvailableTimeSlot(@RequestBody AvailableTimeSlotDTO timeSlotDTO) {
        List<RestaurantTable> tables = reservationSystemService.findAllTables();
        for (RestaurantTable table : tables) {
            AvailableTimeSlot newSlot = new AvailableTimeSlot(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime(), table);
            reservationSystemService.saveAvailableTimeSlot(newSlot);
        }
        return ResponseEntity.ok("Time slots saved successfully!");
    }
}
