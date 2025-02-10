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
import java.util.*;

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

        // Find a set of table cluster where each cluster have enough capacity for number of customer
        List<RestaurantTable> allTables = reservationSystemService.findAllTables();
        List<List<RestaurantTable>> tableClusters = TableClusterFinder.findAllTableClusters(allTables, reservation.getNumOfPeople());

        // Sort table clusters by cluster's total capacity in asc order
        Collections.sort(tableClusters, (cluster1, cluster2) -> {
            int capacityOfCluster1 = 0, capacityOfCluster2 = 0;
            for (RestaurantTable table : cluster1) {
                capacityOfCluster1 += table.getCapacity();
            }
            for (RestaurantTable table : cluster2) {
                capacityOfCluster2 += table.getCapacity();
            }
            return capacityOfCluster1 - capacityOfCluster2;
        });

        for (List<RestaurantTable> tableCluster : tableClusters) {
            System.out.println("--- table cluster ---");
            for (RestaurantTable table : tableCluster) {
                System.out.println("table " + table.getId());
            }
        }
        System.out.println("---------------------");

        if (tableClusters.isEmpty()) {
            System.out.println("There is no table for " + reservation.getNumOfPeople() + " people");
            // TODO: create correct path to html
            return "user/index";
        }

        // Store table (key) and [slot, condition] (value)
        Map<RestaurantTable, List<Object>> tableSlotCondition = new HashMap<>();
        // Iterate found table clusters and search available time slot
        clusterLoop:
        for (List<RestaurantTable> cluster : tableClusters) {
            int counter = 0;
            tableLoop:
            for (RestaurantTable table : cluster) {
                List<AvailableTimeSlot> availableTimeSlots = table.getAvailableTimeSlots();
                if (!availableTimeSlots.isEmpty()) {
                    slotLoop:
                    for (AvailableTimeSlot slot : availableTimeSlots) {
                        if (isBetween(slot.getStartTime(), slot.getEndTime(), reservation.getStartTime(), reservation.getEndTime())) {
                            List<Object> slotAndCondition = new ArrayList<>();
                            if (isTimeSlotBeforeReservationStartTimeTooShort(reservation, slot, durationMinutes) &&
                                    !isTimeSlotAfterReservationStartTimeTooShort(reservation, slot, durationMinutes)) {
                                slotAndCondition.add(slot);
                                slotAndCondition.add("condition1");
                                tableSlotCondition.put(table, slotAndCondition);
                            } else if (!isTimeSlotBeforeReservationStartTimeTooShort(reservation, slot, durationMinutes) &&
                                    isTimeSlotAfterReservationStartTimeTooShort(reservation, slot, durationMinutes)) {
                                slotAndCondition.add(slot);
                                slotAndCondition.add("condition2");
                                tableSlotCondition.put(table, slotAndCondition);
                            } else if (!isTimeSlotBeforeReservationStartTimeTooShort(reservation, slot, durationMinutes) &&
                                    !isTimeSlotAfterReservationStartTimeTooShort(reservation, slot, durationMinutes)) {
                                slotAndCondition.add(slot);
                                slotAndCondition.add("condition3");
                                tableSlotCondition.put(table, slotAndCondition);
                            } else {
                                slotAndCondition.add(slot);
                                slotAndCondition.add("condition4");
                                tableSlotCondition.put(table, slotAndCondition);
                            }

                            counter++;
                            System.out.println("Found slot " + slot.getStartTime()
                                    + " at table " + table.getId());
                            break slotLoop; // available slot is found, no iteration for this table needed anymore
                        }
                    }
                } else {
                    tableSlotCondition.clear();
                    System.out.println("No available time slot at table " + table.getId());
                    break tableLoop; // search for next cluster
                }

                // All table in the cluster are reservable at customer's reservation time
                if (counter == cluster.size()) {
                    break clusterLoop;
                }
            }
        }

        if (tableSlotCondition.isEmpty()) {
            System.out.println("There is no available time slot from " + reservation.getStartTime());
            // TODO: create correct path to html
            return "user/index";
        }

        // Update available_time_slot table in DB
        for (Map.Entry<RestaurantTable, List<Object>> entry : tableSlotCondition.entrySet()) {
            RestaurantTable table = entry.getKey();
            AvailableTimeSlot slot = (AvailableTimeSlot) entry.getValue().get(0);
            String condition = (String) entry.getValue().get(1);

            reservation.addTables(table);

            switch (condition) {
                case "condition1" -> {
                    slot.setStartTime(reservation.getEndTime());
                    reservationSystemService.updateAvailableTimeSlot(slot);
                }
                case "condition2" -> {
                    slot.setEndTime(reservation.getStartTime());
                    reservationSystemService.updateAvailableTimeSlot(slot);
                }
                case "condition3" -> {
                    AvailableTimeSlot newSlot1 = new AvailableTimeSlot(slot.getStartTime(), reservation.getStartTime(), table);
                    AvailableTimeSlot newSlot2 = new AvailableTimeSlot(reservation.getEndTime(), slot.getEndTime(), table);
                    reservationSystemService.saveAvailableTimeSlot(newSlot1);
                    reservationSystemService.saveAvailableTimeSlot(newSlot2);
                    reservationSystemService.deleteAvailableTimeSlot(slot.getId());
                }
                case "condition4" -> {
                    reservationSystemService.deleteAvailableTimeSlot(slot.getId());
                }
                default -> System.out.println("Not proper condition is set");
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
