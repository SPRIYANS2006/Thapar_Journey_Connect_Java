package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.services.AuthService;
import com.tiet.journeyconnect.services.MyJourneysService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/my-journeys")
public class MyJourneysController {

    @Autowired
    private MyJourneysService myJourneysService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyJourneys(HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(myJourneysService.getMyJourneysFeed(user));
    }
}
