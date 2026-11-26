package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.dto.TravelDtos.*;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.services.AuthService;
import com.tiet.journeyconnect.services.TravelGroupService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/travel-groups")
public class TravelGroupController {

    @Autowired
    private TravelGroupService travelGroupService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getTravelGroups(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "communityId", required = false) String communityId,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "excludeJoined", required = false) Boolean excludeJoined,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(travelGroupService.getTravelGroups(type, communityId, search, date, excludeJoined, user));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Map<String, Object>>> getMyTravelGroups(HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(travelGroupService.getMyTravelGroups(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTravelGroupById(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(travelGroupService.getTravelGroupById(id, user));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTravelGroup(
            @RequestBody CreateTravelGroupRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.status(201).body(travelGroupService.createTravelGroup(req, user));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Map<String, Object>> joinTravelGroup(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(travelGroupService.joinTravelGroup(id, user));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Map<String, Object>> leaveTravelGroup(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(travelGroupService.leaveTravelGroup(id, user));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<Map<String, Object>>> getMessages(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(travelGroupService.getMessages(id, user));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @PathVariable("id") String id,
            @RequestBody SendMessageRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(travelGroupService.sendMessage(id, req, user));
    }
}
