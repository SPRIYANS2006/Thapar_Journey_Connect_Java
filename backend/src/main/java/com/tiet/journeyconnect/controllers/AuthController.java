package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.dto.AuthDtos.*;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.services.AuthService;
import com.tiet.journeyconnect.services.SerializerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SerializerService serializerService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(req, response));
    }

    @PostMapping("/google")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody GoogleAuthRequest req, HttpServletResponse response) {
        return ResponseEntity.ok(authService.googleLogin(req, response));
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @RequestBody PasswordChangeRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(authService.updatePassword(req, user, response));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        String token = authService.extractToken(request);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("token", token);
        body.put("user", serializerService.serializeUser(user));
        return ResponseEntity.ok(body);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody UpdateProfileRequest req,
            HttpServletRequest request) {
        UserEntity user = authService.resolveUser(request, true);
        return ResponseEntity.ok(authService.updateProfile(req, user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(request, response));
    }
}
