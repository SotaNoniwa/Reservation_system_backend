package com.myproject.reservationsystem.controller;

import com.myproject.reservationsystem.entity.*;
import com.myproject.reservationsystem.security.CustomUserDetails;
import com.myproject.reservationsystem.service.ReservationSystemService;
import com.myproject.reservationsystem.service.UserService;
import com.myproject.reservationsystem.util.TableClusterFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private ReservationSystemService reservationSystemService;

    private UserService userService;

    @Autowired
    public UserController(ReservationSystemService reservationSystemService, UserService userService) {
        this.reservationSystemService = reservationSystemService;
        this.userService = userService;
    }

    @GetMapping
    public String showUserPanel() {
        return "user/index";
    }

    @GetMapping("/reservation")
    public String showReservationForm(Model model) {
        Reservation reservation = new Reservation();
        reservation.setCourse(new Course());
        model.addAttribute("reservation", reservation);

        List<Course> courses = reservationSystemService.findAllCourses();
        model.addAttribute("courses", courses);

        return "user/reservation-form";
    }

    @PostMapping("/reservation")
    public String processReservationForm(@ModelAttribute("reservation") Reservation reservation) {
        // Retrieve currently logging user info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            int userId = userDetails.getId();
            User user = userService.findUserById(userId);
            reservation.setUser(user);
        } else {
            throw new RuntimeException("User not authenticated properly.");
        }

        // Calculate reservation duration
        long durationMinutes = 120;
        LocalDateTime endTime = reservation.getStartTime().plusMinutes(durationMinutes);
        reservation.setEndTime(endTime);

        // Associate corresponding course
        int courseId = reservation.getCourse().getId();
        Course course = reservationSystemService.findCourseById(courseId);
        reservation.setCourse(course);

        // TODO: if numOfPeople > tableMaxCap, then let them reserve separate tables
        List<RestaurantTable> tables = reservationSystemService.findTablesByCapacity(reservation.getNumOfPeople());

        if (tables == null) {
            System.out.println("There is no table for " + reservation.getNumOfPeople() + " people");
            // TODO: create correct path to html
            return "test";
        }

        outerLoop:
        for (RestaurantTable table : tables) {
            List<AvailableTimeSlot> availableTimeSlots = table.getAvailableTimeSlots();
            if (!availableTimeSlots.isEmpty()) {
                for (AvailableTimeSlot slot : availableTimeSlots) {
                    if (isBetween(slot.getStartTime(), slot.getEndTime(), reservation.getStartTime(), reservation.getEndTime())) {
                        reservation.addTables(table);
                        if (isTimeSlotBeforeReservationStartTimeTooShort(reservation, slot, durationMinutes) &&
                                !isTimeSlotAfterReservationStartTimeTooShort(reservation, slot, durationMinutes)) {
                            slot.setStartTime(reservation.getEndTime());
                            reservationSystemService.updateAvailableTimeSlot(slot);
                        } else if (!isTimeSlotBeforeReservationStartTimeTooShort(reservation, slot, durationMinutes) &&
                                isTimeSlotAfterReservationStartTimeTooShort(reservation, slot, durationMinutes)) {
                            slot.setEndTime(reservation.getStartTime());
                            reservationSystemService.updateAvailableTimeSlot(slot);
                        } else if (!isTimeSlotBeforeReservationStartTimeTooShort(reservation, slot, durationMinutes) &&
                                !isTimeSlotAfterReservationStartTimeTooShort(reservation, slot, durationMinutes)) {
                            AvailableTimeSlot newSlot1 = new AvailableTimeSlot(slot.getStartTime(), reservation.getStartTime(), table);
                            AvailableTimeSlot newSlot2 = new AvailableTimeSlot(reservation.getEndTime(), slot.getEndTime(), table);
                            reservationSystemService.saveAvailableTimeSlot(newSlot1);
                            reservationSystemService.saveAvailableTimeSlot(newSlot2);
                            reservationSystemService.deleteAvailableTimeSlot(slot.getId());
                        } else {
                            reservationSystemService.deleteAvailableTimeSlot(slot.getId());
                        }
                        break outerLoop;
                    }
                }
            }
        }

        reservationSystemService.saveReservation(reservation);

        return "user/confirmation";
    }

    private boolean isBetween(LocalDateTime startRange, LocalDateTime endRange, LocalDateTime startTime, LocalDateTime endTime) {
        return (startTime.isEqual(startRange) || startTime.isAfter(startRange))
                && (endTime.isEqual(endRange) || endTime.isBefore(endRange));
    }

    /**
     * Check if there is still enough time before reservation start time to reserve a table again
     * E.g. a customer reserve a table 11:00 to 13:00, availableTimeSlot is 10:00-20:00,
     * then availableTimeSlot should be updated to 13:00-20:00.
     * The table has 10:00-11:00 free slot but reservation duration is 120 min, so there is not enough time before reservation start time.
     **/
    private boolean isTimeSlotBeforeReservationStartTimeTooShort(Reservation reservation, AvailableTimeSlot slot, long durationMinutes) {
        return reservation.getStartTime().minusMinutes(durationMinutes).isBefore(slot.getStartTime());
    }

    private boolean isTimeSlotAfterReservationStartTimeTooShort(Reservation reservation, AvailableTimeSlot slot, long durationMinutes) {
        return reservation.getEndTime().plusMinutes(durationMinutes).isAfter(slot.getEndTime());
    }
}
