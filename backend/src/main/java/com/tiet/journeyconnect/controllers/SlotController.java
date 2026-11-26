package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.dto.TravelDtos.*;
import com.tiet.journeyconnect.models.RideSlot;
import com.tiet.journeyconnect.models.entities.DestinationEntity;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.repositories.DestinationRepository;
import com.tiet.journeyconnect.services.AuthService;
import com.tiet.journeyconnect.services.SlotEngineService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    @Autowired
    private SlotEngineService slotEngineService;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSlots(
            @RequestParam(name = "destinationId", required = false) String destinationId,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "vehicle", required = false) String vehicle,
            @RequestParam(name = "direction", required = false) String direction,
            HttpServletRequest request) {

        if (destinationId == null || destinationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination ID is required.");
        }

        UserEntity currentUser = authService.resolveUser(request, false);
        String currentUserId = currentUser != null ? currentUser.getId() : null;

        String travelDate = SlotEngineService.normalizeDate(date);
        boolean isReturn = "returning".equalsIgnoreCase(direction);
        String tripDirection = isReturn ? "returning" : "departing";

        Optional<DestinationEntity> destOpt = destinationRepository.findById(destinationId);
        String destName = destOpt.map(DestinationEntity::getName).orElse(destinationId);
        String avgCabFare = destOpt.map(DestinationEntity::getAvgCabFare).orElse("₹200 total");

        List<RideSlot> slots = slotEngineService.generate96Slots(destinationId, travelDate, vehicle, direction, currentUserId);

        long availableCount = slots.stream().filter(s -> !s.isPast() && !s.isFull()).count();

        Map<String, Object> destMap = new LinkedHashMap<>();
        destMap.put("id", destinationId);
        destMap.put("name", destName);
        destMap.put("avgCabFare", avgCabFare);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("destination", destMap);
        body.put("date", travelDate);
        body.put("direction", tripDirection);
        body.put("origin", isReturn ? destName : "Thapar Institute");
        body.put("vehicle", vehicle != null ? vehicle : "Either");
        body.put("totalSlots", slots.size());
        body.put("availableSlotsCount", availableCount);
        body.put("slots", slots);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinSlot(
            @RequestBody JoinSlotRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(slotEngineService.joinSlot(req, user));
    }

    @PostMapping("/reschedule/preview")
    public ResponseEntity<Map<String, Object>> previewReschedule(
            @RequestBody ReschedulePreviewRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(slotEngineService.previewReschedule(req.getOldGroupId(), user));
    }

    @PostMapping("/reschedule")
    public ResponseEntity<Map<String, Object>> rescheduleSlot(
            @RequestBody RescheduleSlotRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(slotEngineService.rescheduleSlot(req, user));
    }
}
