package com.tiet.journeyconnect;

import com.tiet.journeyconnect.dto.TravelDtos.JoinSlotRequest;
import com.tiet.journeyconnect.exceptions.SingleRidePolicyViolationException;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.repositories.UserRepository;
import com.tiet.journeyconnect.services.GroupService;
import com.tiet.journeyconnect.services.SlotEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Test Suite verifying Single-Ride Policy enforcement across active bookings.
 */
@SpringBootTest
public class SingleRidePolicyTest {

    @Autowired
    private SlotEngineService slotEngineService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity student;
    private final String travelDate = LocalDate.now().plusDays(3).toString();

    @BeforeEach
    void setup() {
        student = userRepository.findById("student_single_policy").orElseGet(() -> {
            UserEntity u = new UserEntity();
            u.setId("student_single_policy");
            u.setName("Policy Test Student");
            u.setEmail("policy.test@thapar.edu");
            u.setHostel("Tejas Hall (Hostel-J)");
            u.setReliability(5.0);
            u.setTripsCompleted(0);
            return userRepository.save(u);
        });
        student.setActiveGroupId(null);
        userRepository.save(student);
    }

    @Test
    @DisplayName("Single-Ride Policy: Joining a second active travel slot throws SingleRidePolicyViolationException")
    void testSingleRidePolicyEnforcement() {
        // Step 1: Join Slot A (Patiala Railway Station at 10:00)
        JoinSlotRequest slotA = new JoinSlotRequest();
        slotA.setDestinationId("railway");
        slotA.setDate(travelDate);
        slotA.setTime("10:00");
        slotA.setVehicle("Shared Cab");

        Map<String, Object> resultA = slotEngineService.joinSlot(slotA, student);
        assertNotNull(resultA);
        assertTrue((Boolean) resultA.get("success"));

        // Step 2: Attempt to book Slot B (Chandigarh Airport at 16:00) while enrolled in Slot A
        JoinSlotRequest slotB = new JoinSlotRequest();
        slotB.setDestinationId("chd_airport");
        slotB.setDate(travelDate);
        slotB.setTime("16:00");
        slotB.setVehicle("Shared Cab");

        assertThrows(SingleRidePolicyViolationException.class, () -> slotEngineService.joinSlot(slotB, student),
                "Student enrolled in active ride A cannot join ride B without leaving or rescheduling");

        // Step 3: Leave Slot A
        String groupIdA = student.getActiveGroupId();
        assertNotNull(groupIdA);
        groupService.leaveGroup(groupIdA, student);

        // Step 4: Now student can freely join Slot B
        Map<String, Object> resultB = slotEngineService.joinSlot(slotB, student);
        assertNotNull(resultB);
        assertTrue((Boolean) resultB.get("success"));
    }
}
