package com.tiet.journeyconnect;

import com.tiet.journeyconnect.models.RideSlot;
import com.tiet.journeyconnect.services.SlotEngineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Test Suite for the 15-minute departure slot engine and two-way direction separation.
 */
@SpringBootTest
public class SlotEngineTest {

    @Autowired
    private SlotEngineService slotEngineService;

    @Test
    @DisplayName("Slot Engine: Generates exactly 96 departure intervals per 24-hour day (15-min increments)")
    void testGenerates96SlotsDaily() {
        String testDate = LocalDate.now().plusDays(1).toString();
        List<RideSlot> slots = slotEngineService.generate96Slots("railway", testDate, "Shared Cab", "departing", null);

        assertNotNull(slots);
        assertEquals(96, slots.size(), "A 24-hour day must have exactly 96 discrete 15-minute slots (24 * 4)");

        // Verify start and end boundaries
        assertEquals("00:00", slots.get(0).getTime());
        assertEquals("00:15", slots.get(1).getTime());
        assertEquals("23:45", slots.get(95).getTime());
    }

    @Test
    @DisplayName("Two-Way Routing: Departing and Returning routes generate distinct slot keys")
    void testTwoWayRoutingSlotKeyDistinctness() {
        String testDate = LocalDate.now().plusDays(1).toString();

        List<RideSlot> departingSlots = slotEngineService.generate96Slots("chd_airport", testDate, "Shared Cab", "departing", null);
        List<RideSlot> returningSlots = slotEngineService.generate96Slots("chd_airport", testDate, "Shared Cab", "returning", null);

        assertEquals(96, departingSlots.size());
        assertEquals(96, returningSlots.size());

        RideSlot dep1000 = departingSlots.stream().filter(s -> "10:00".equals(s.getTime())).findFirst().orElseThrow();
        RideSlot ret1000 = returningSlots.stream().filter(s -> "10:00".equals(s.getTime())).findFirst().orElseThrow();

        assertNotEquals(dep1000.getSlotKey(), ret1000.getSlotKey(),
                "Departing and Returning slot keys must be distinct to prevent routing collisions");
        assertTrue(ret1000.getSlotKey().contains("_ret_"), "Returning slot key must contain '_ret_' tag");
        assertEquals("Thapar Institute", dep1000.getOrigin());
        assertEquals("Chandigarh Airport", dep1000.getDestinationName());
        assertEquals("Chandigarh Airport", ret1000.getOrigin());
        assertEquals("Thapar Institute", ret1000.getDestinationName());
    }
}
