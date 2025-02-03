package com.myproject.reservationsystem.controller;

import com.myproject.reservationsystem.entity.AvailableTimeSlot;
import com.myproject.reservationsystem.entity.RestaurantTable;
import com.myproject.reservationsystem.service.ReservationSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private ReservationSystemService reservationSystemService;

    @Autowired
    public AdminController(ReservationSystemService reservationSystemService) {
        this.reservationSystemService = reservationSystemService;
    }

    @GetMapping
    public String showAdminPanel() {
        return "admin/index";
    }

    @GetMapping("/available-time-slot")
    public String showAvailableTimeSlotForm(Model model) {
        AvailableTimeSlot availableTimeSlot = new AvailableTimeSlot();
        model.addAttribute("slot", availableTimeSlot);

        return "admin/available-time-slot-form";
    }

    @PostMapping("/available-time-slot")
    public String processAvailableTimeSlotForm(
            @ModelAttribute("slot") AvailableTimeSlot availableTimeSlot) {
        LocalDateTime startTime = availableTimeSlot.getStartTime();
        LocalDateTime endTime = availableTimeSlot.getEndTime();

        List<RestaurantTable> tables = reservationSystemService.findAllTables();
        for (RestaurantTable table : tables) {
            AvailableTimeSlot newSlot = new AvailableTimeSlot(startTime, endTime, table);
            reservationSystemService.saveAvailableTimeSlot(newSlot);
        }

        return "admin/confirmation";
    }
}
