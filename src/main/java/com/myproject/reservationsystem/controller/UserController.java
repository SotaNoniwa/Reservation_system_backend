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
    public String processReservationForm(@ModelAttribute("reservation") Reservation reservation, Model model) {
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

        if (tableClusters.isEmpty()) {
            System.out.println("There is no table for " + reservation.getNumOfPeople() + " people");
            model.addAttribute("msg", "There is no table for " + reservation.getNumOfPeople() + " people");
            return "error";
        }

        sortTableClustersByCapacity(tableClusters);

        System.out.println("---- Found table clusters ----");
        for (List<RestaurantTable> cluster: tableClusters) {
            StringBuilder sb = new StringBuilder("[ ");
            for (RestaurantTable table : cluster) {
                sb.append(table.getId() + " ");
            }
            System.out.println("Table cluster: " + sb + "]");
        }

        // <RestaurantTable, [AvailableTimeSlot, Integer]>
        Map<RestaurantTable, List<Object>> tableSlotCondition = findAvailableTimeSlots(tableClusters, reservation, durationMinutes);

        if (tableSlotCondition.isEmpty()) {
            System.out.println("There is no available time slot from " + reservation.getStartTime());
            model.addAttribute("msg", "There is no available time slot from " + reservation.getStartTime());
            return "error";
        }

        updateAvailableTimeSlot(tableSlotCondition, reservation);

        reservationSystemService.saveReservation(reservation);

        return "user/confirmation";
    }

    private void sortTableClustersByCapacity(List<List<RestaurantTable>> tableClusters) {
        tableClusters.sort(Comparator.comparingInt(cluster ->
                cluster.stream().mapToInt(RestaurantTable::getCapacity).sum()));
    }

    private Map<RestaurantTable, List<Object>> findAvailableTimeSlots(
            List<List<RestaurantTable>> tableClusters, Reservation reservation, long durationMinutes
    ) {
        Map<RestaurantTable, List<Object>> tableSlotCondition = new HashMap<>();

        // Iterate clusters, search available time slot for each table in cluster
        for (List<RestaurantTable> cluster : tableClusters) {
            int numOfReservableTables = 0; // Keep track of found reservable table

            System.out.println("---- searching available time slot ----");
            StringBuilder sb = new StringBuilder("Cluster: [ ");
            for (RestaurantTable table : cluster) {
                sb.append(table.getId() + " ");
            }
            sb.append("]");
            System.out.println(sb);

            for (RestaurantTable table : cluster) {
                List<AvailableTimeSlot> availableTimeSlots = table.getAvailableTimeSlots();

                // The table has no available time slot, so discard current cluster from candidates
                if (availableTimeSlots.isEmpty()) {
                    tableSlotCondition.clear();
                    System.out.println("Table " + table.getId() + " has no available time slot...");
                    break; // Search for next cluster
                }

                for (AvailableTimeSlot slot : availableTimeSlots) {
                    if (isBetween(slot.getStartTime(), slot.getEndTime(), reservation.getStartTime(), reservation.getEndTime())) {
                        int condition = determineCondition(reservation, slot, durationMinutes);
                        tableSlotCondition.put(table, Arrays.asList(slot, condition));
                        numOfReservableTables++;
                        System.out.println("Table " + table.getId() + " is reservable at " + slot.getStartTime());
                        // Available slot is found, search for available slot for next table in the same cluster
                        break;
                    }
                }

                // All table in the cluster are reservable at customer's reservation time
                if (numOfReservableTables == cluster.size()) {
                    return tableSlotCondition;
                }
            }
        }
        return tableSlotCondition;
    }

    private int determineCondition(Reservation reservation, AvailableTimeSlot slot, long durationMinutes) {
        boolean beforeTooShort = isTimeSlotBeforeReservationStartTimeTooShort(reservation, slot, durationMinutes);
        boolean afterTooShort = isTimeSlotAfterReservationStartTimeTooShort(reservation, slot, durationMinutes);

        if (beforeTooShort && !afterTooShort) return 1;
        if (!beforeTooShort && afterTooShort) return 2;
        if (!beforeTooShort && !afterTooShort) return 3;
        return 4;
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

    private void updateAvailableTimeSlot(Map<RestaurantTable, List<Object>> tableSlotCondition, Reservation reservation) {
        for (Map.Entry<RestaurantTable, List<Object>> entry : tableSlotCondition.entrySet()) {
            RestaurantTable table = entry.getKey();
            AvailableTimeSlot slot = (AvailableTimeSlot) entry.getValue().get(0);
            int condition = (int) entry.getValue().get(1);

            reservation.addTables(table);

            switch (condition) {
                case 1 -> {
                    slot.setStartTime(reservation.getEndTime());
                    reservationSystemService.updateAvailableTimeSlot(slot);
                }
                case 2 -> {
                    slot.setEndTime(reservation.getStartTime());
                    reservationSystemService.updateAvailableTimeSlot(slot);
                }
                case 3 -> {
                    AvailableTimeSlot newSlot1 = new AvailableTimeSlot(slot.getStartTime(), reservation.getStartTime(), table);
                    AvailableTimeSlot newSlot2 = new AvailableTimeSlot(reservation.getEndTime(), slot.getEndTime(), table);
                    reservationSystemService.saveAvailableTimeSlot(newSlot1);
                    reservationSystemService.saveAvailableTimeSlot(newSlot2);
                    reservationSystemService.deleteAvailableTimeSlot(slot.getId());
                }
                case 4 -> {
                    reservationSystemService.deleteAvailableTimeSlot(slot.getId());
                }
                default -> System.out.println("Not proper condition is set");
            }
        }
    }

}
