package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.dto.SafetyDtos.*;
import com.tiet.journeyconnect.models.entities.SafetyReportEntity;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.services.AuthService;
import com.tiet.journeyconnect.services.SafetyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/safety")
public class SafetyController {

    @Autowired
    private SafetyService safetyService;

    @Autowired
    private AuthService authService;

    @PostMapping("/reports")
    public ResponseEntity<Map<String, Object>> submitReport(
            @RequestBody SafetyReportRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.status(201).body(safetyService.submitReport(req, user));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<SafetyReportEntity>> getReports(HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(safetyService.getReports(user));
    }

    @PatchMapping("/reports/{id}")
    public ResponseEntity<SafetyReportEntity> reviewReport(
            @PathVariable("id") String id,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(safetyService.reviewReport(id, user));
    }

    @PutMapping("/mute")
    public ResponseEntity<Map<String, Object>> muteUser(
            @RequestBody MuteUserRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(safetyService.muteUser(req, user));
    }
}
