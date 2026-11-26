package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.dto.CommunityDtos.*;
import com.tiet.journeyconnect.dto.TravelDtos.*;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.services.AuthService;
import com.tiet.journeyconnect.services.CommunityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCommunities() {
        return ResponseEntity.ok(communityService.getCommunities());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCommunity(
            @RequestBody CreateCommunityRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.status(201).body(communityService.createCommunity(req, user));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Map<String, Object>> joinCommunity(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(communityService.joinCommunity(id, user));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Map<String, Object>> leaveCommunity(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(communityService.leaveCommunity(id, user));
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<Map<String, Object>>> getCommunityPosts(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(communityService.getCommunityPosts(id, user));
    }

    @PostMapping("/{id}/posts")
    public ResponseEntity<Map<String, Object>> createCommunityPost(
            @PathVariable("id") String id,
            @RequestBody CreatePostRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.status(201).body(communityService.createCommunityPost(id, req, user));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLikePost(
            @PathVariable("postId") String postId,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(communityService.toggleLikePost(postId, user));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable("postId") String postId,
            @RequestBody AddCommentRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(communityService.addComment(postId, req, user));
    }

    @GetMapping("/{id}/rides")
    public ResponseEntity<List<Map<String, Object>>> getCityRides(
            @PathVariable("id") String id,
            @RequestParam(name = "mode", required = false) String mode,
            @RequestParam(name = "date", required = false) String date,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(communityService.getCityRides(id, mode, date, user));
    }

    @PostMapping("/{id}/rides")
    public ResponseEntity<Map<String, Object>> createCityRide(
            @PathVariable("id") String id,
            @RequestBody CreateCityRideRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.status(201).body(communityService.createCityRide(id, req, user));
    }

    @PostMapping("/{id}/rides/{rideId}/join")
    public ResponseEntity<Map<String, Object>> joinCityRide(
            @PathVariable("id") String id,
            @PathVariable("rideId") String rideId,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        // Delegate to joinGroup which handles both groups and city rides
        return ResponseEntity.ok(communityService.joinCommunity(id, user));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<Map<String, Object>> getCityCommunitySlots(
            @PathVariable("id") String id,
            @RequestParam(name = "destination", required = false) String destination,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "mode", required = false) String mode,
            @RequestParam(name = "direction", required = false) String direction,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, false);
        List<Map<String, Object>> slots = communityService.getCityCommunitySlots(id, destination, date, mode, direction, user);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("slots", slots);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{id}/slots/join")
    public ResponseEntity<Map<String, Object>> joinCityCommunitySlot(
            @PathVariable("id") String id,
            @RequestBody JoinCityCommunitySlotRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.status(201).body(communityService.joinCityCommunitySlot(id, req, user));
    }
}
