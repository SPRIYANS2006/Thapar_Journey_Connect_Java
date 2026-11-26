package com.tiet.journeyconnect;

import com.tiet.journeyconnect.dto.TravelDtos.JoinSlotRequest;
import com.tiet.journeyconnect.exceptions.OverbookingException;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.repositories.GroupMemberRepository;
import com.tiet.journeyconnect.repositories.GroupRepository;
import com.tiet.journeyconnect.repositories.UserRepository;
import com.tiet.journeyconnect.services.SlotEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Multithreading & Concurrency Control Test Suite.
 * Simulates concurrent student booking requests on the same slot key
 * using ExecutorService, CountDownLatch, and thread synchronization.
 */
@SpringBootTest
public class ConcurrencyBookingTest {

    @Autowired
    private SlotEngineService slotEngineService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    private final String testDate = LocalDate.now().plusDays(2).toString();
    private final String testTime = "15:00";

    @BeforeEach
    void setupUsers() {
        // Ensure 5 distinct test users exist in database
        for (int i = 1; i <= 5; i++) {
            String uid = "concurrent_user_" + i;
            if (userRepository.findById(uid).isEmpty()) {
                UserEntity u = new UserEntity();
                u.setId(uid);
                u.setName("Concurrent Rider " + i);
                u.setEmail("concurrent." + i + "@thapar.edu");
                u.setHostel("Tejas Hall (Hostel-J)");
                u.setReliability(5.0);
                u.setTripsCompleted(0);
                userRepository.save(u);
            }
        }
    }

    @Test
    @DisplayName("Concurrency: Simultaneous booking for the 4th/last seat preserves capacity (rejects 5th member)")
    void testConcurrentBookingCapacityProtection() throws InterruptedException {
        // User 1 forms the slot
        UserEntity u1 = userRepository.findById("concurrent_user_1").orElseThrow();
        JoinSlotRequest req1 = new JoinSlotRequest();
        req1.setDestinationId("chd_airport");
        req1.setDate(testDate);
        req1.setTime(testTime);
        req1.setVehicle("Shared Cab");
        slotEngineService.joinSlot(req1, u1);

        // User 2 joins
        UserEntity u2 = userRepository.findById("concurrent_user_2").orElseThrow();
        slotEngineService.joinSlot(req1, u2);

        // User 3 joins (Now 3 seats filled, exactly 1 seat left)
        UserEntity u3 = userRepository.findById("concurrent_user_3").orElseThrow();
        slotEngineService.joinSlot(req1, u3);

        // Now: User 4 and User 5 attempt to book the single remaining seat AT THE EXACT SAME MILLISECOND
        UserEntity u4 = userRepository.findById("concurrent_user_4").orElseThrow();
        UserEntity u5 = userRepository.findById("concurrent_user_5").orElseThrow();

        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch readyLatch = new CountDownLatch(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger overbookingCount = new AtomicInteger(0);
        List<Throwable> unexpectedErrors = new CopyOnWriteArrayList<>();

        java.util.function.Function<UserEntity, Runnable> createBookTask = user -> () -> {
            readyLatch.countDown();
            try {
                startLatch.await(); // Guarantee simultaneous release
                slotEngineService.joinSlot(req1, user);
                successCount.incrementAndGet();
            } catch (OverbookingException ex) {
                overbookingCount.incrementAndGet();
            } catch (Throwable t) {
                unexpectedErrors.add(t);
            }
        };

        executor.submit(createBookTask.apply(u4));
        executor.submit(createBookTask.apply(u5));

        // Wait until both threads are primed and ready
        readyLatch.await(5, TimeUnit.SECONDS);
        // Release both threads simultaneously to cause a race condition
        startLatch.countDown();

        executor.shutdown();
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        assertTrue(finished, "Booking threads finished within time limit");

        assertTrue(unexpectedErrors.isEmpty(), "No unexpected errors occurred: " + unexpectedErrors);

        // Exactly one thread succeeded to claim the 4th seat
        assertEquals(1, successCount.get(), "Exactly one user claimed the 4th seat");
        // The other thread received an OverbookingException
        assertEquals(1, overbookingCount.get(), "The losing thread was safely rejected with OverbookingException");
    }
}
