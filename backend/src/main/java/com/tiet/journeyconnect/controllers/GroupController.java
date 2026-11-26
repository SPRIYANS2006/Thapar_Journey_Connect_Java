package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.dto.TravelDtos.*;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.services.AuthService;
import com.tiet.journeyconnect.services.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getGroups(HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.getMyGroups(user));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Map<String, Object>>> getMyGroups(HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.getMyGroups(user));
    }

    @GetMapping("/history/mine")
    public ResponseEntity<List<Map<String, Object>>> getGroupHistory(HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.getGroupHistory(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGroupById(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.getGroupById(id, user));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Map<String, Object>> joinGroup(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.joinGroup(id, user));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Map<String, Object>> leaveGroup(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.leaveGroup(id, user));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmGroup(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.confirmGroup(id, user));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeGroup(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.completeGroup(id, user));
    }

    @PostMapping("/{id}/pickup-point")
    public ResponseEntity<Map<String, Object>> changePickupPoint(
            @PathVariable("id") String id,
            @RequestBody ChangePickupRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.changePickupPoint(id, req, user));
    }

    @PostMapping("/{id}/friends")
    public ResponseEntity<Map<String, Object>> addFriend(
            @PathVariable("id") String id,
            @RequestBody AddFriendRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.addFriend(id, req, user));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Map<String, Object>> removeFriend(
            @PathVariable("id") String id,
            @PathVariable("friendId") String friendId,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.removeFriend(id, friendId, user));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<Map<String, Object>>> getMessages(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.getMessages(id, user));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @PathVariable("id") String id,
            @RequestBody SendMessageRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(groupService.sendMessage(id, req, user));
    }
}
