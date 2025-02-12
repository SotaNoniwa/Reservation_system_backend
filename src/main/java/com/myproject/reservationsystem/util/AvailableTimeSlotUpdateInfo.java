package com.myproject.reservationsystem.util;

import com.myproject.reservationsystem.entity.AvailableTimeSlot;
import com.myproject.reservationsystem.entity.RestaurantTable;

public record AvailableTimeSlotUpdateInfo(
        RestaurantTable table, AvailableTimeSlot slot, RemainingTimeSlotPattern remainingSlotPattern
) {
}
