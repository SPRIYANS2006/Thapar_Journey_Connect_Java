package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.services.AuthService;
import com.tiet.journeyconnect.services.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthService authService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents(HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, false);
        if (user == null) {
            SseEmitter unauth = new SseEmitter(1000L);
            try {
                unauth.send(SseEmitter.event().name("error").data("Unauthorized"));
                unauth.complete();
            } catch (Exception ignored) {}
            return unauth;
        }
        return notificationService.registerEmitter(user.getId());
    }

    @GetMapping("/poll")
    public ResponseEntity<Map<String, Object>> pollEvents(
            @RequestParam(name = "since", defaultValue = "0") long since,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        List<Map<String, Object>> events = notificationService.getEventsForUser(user.getId(), since);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("events", events);
        res.put("serverTime", System.currentTimeMillis());
        return ResponseEntity.ok(res);
    }
}
