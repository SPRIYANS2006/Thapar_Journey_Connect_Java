package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.config.JwtTokenProvider;
import com.tiet.journeyconnect.dto.AuthDtos.*;
import com.tiet.journeyconnect.exceptions.InvalidCampusDomainException;
import com.tiet.journeyconnect.exceptions.UnauthorizedAccessException;
import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SerializerService serializerService;

    public void setSessionCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        response.addCookie(cookie);
    }

    public void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("token".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        String paramToken = request.getParameter("token");
        if (paramToken != null && !paramToken.trim().isEmpty()) {
            return paramToken.trim();
        }
        return null;
    }

    public UserEntity resolveUser(HttpServletRequest request, boolean required) {
        String token = extractToken(request);
        if (token == null || token.trim().isEmpty()) {
            if (required) {
                throw new UnauthorizedAccessException("Authentication required: No token provided.", 401);
            }
            return null;
        }

        if (!jwtTokenProvider.validateToken(token)) {
            if (required) {
                throw new UnauthorizedAccessException("Authentication failed: Invalid or expired token.", 401);
            }
            return null;
        }

        String userId = jwtTokenProvider.getUserIdFromToken(token);
        Integer tokenVersion = jwtTokenProvider.getTokenVersion(token);

        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            if (required) {
                throw new UnauthorizedAccessException("User session expired or user not found.", 401);
            }
            return null;
        }

        UserEntity user = userOpt.get();
        if (Boolean.TRUE.equals(user.getDisabled())) {
            throw new UnauthorizedAccessException("This account is disabled.", 403);
        }

        if (tokenVersion != null && !tokenVersion.equals(user.getTokenVersion())) {
            if (required) {
                throw new UnauthorizedAccessException("Session has been revoked. Please sign in again.", 401);
            }
            return null;
        }

        return user;
    }

    public Map<String, Object> login(LoginRequest req, HttpServletResponse response) {
        if (req.getEmail() == null || req.getPassword() == null) {
            throw new IllegalArgumentException("Please provide both email and password.");
        }

        String cleanEmail = req.getEmail().toLowerCase().trim();
        if (!cleanEmail.endsWith("@thapar.edu")) {
            throw new InvalidCampusDomainException(cleanEmail, "thapar.edu");
        }

        Optional<UserEntity> userOpt = userRepository.findByEmailIgnoreCase(cleanEmail);
        if (userOpt.isEmpty() || Boolean.TRUE.equals(userOpt.get().getDisabled()) || userOpt.get().getPasswordHash() == null) {
            throw new UnauthorizedAccessException("Invalid email or password. If you haven't set a password yet, please sign in with Google first.", 401);
        }

        UserEntity user = userOpt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedAccessException("Invalid email or password.", 401);
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());
        setSessionCookie(response, token);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("token", token);
        result.put("user", serializerService.serializeUser(user));
        return result;
    }

    @Transactional
    public Map<String, Object> googleLogin(GoogleAuthRequest req, HttpServletResponse response) {
        String credential = req.getCredential();
        if (credential == null || credential.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing Google ID token credential in request body.");
        }

        String email = null;
        String name = "Campus Student";

        // 1. Try decoding standard Google JWT token (header.payload.signature)
        if (credential.contains(".")) {
            String[] parts = credential.split("\\.");
            if (parts.length >= 2) {
                try {
                    byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
                    String payloadJson = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(payloadJson);

                    if (node.has("email") && !node.get("email").asText().isEmpty()) {
                        email = node.get("email").asText().toLowerCase().trim();
                    }
                    if (node.has("name") && !node.get("name").asText().isEmpty()) {
                        name = node.get("name").asText().trim();
                    } else if (node.has("given_name")) {
                        name = node.get("given_name").asText().trim();
                        if (node.has("family_name") && !node.get("family_name").asText().isEmpty()) {
                            name += " " + node.get("family_name").asText().trim();
                        }
                    }
                } catch (Exception ignored) {
                    // Fallback to plain string extraction if decoding fails
                }
            }
        }

        // 2. If credential was passed directly as an email string (e.g. simulated/test mode)
        if (email == null) {
            if (credential.contains("@")) {
                email = credential.trim().toLowerCase();
                name = email.split("@")[0];
            } else {
                throw new IllegalArgumentException("Could not extract email from Google credential.");
            }
        }

        Optional<UserEntity> userOpt = userRepository.findByEmailIgnoreCase(email);
        UserEntity user;
        if (userOpt.isEmpty()) {
            user = new UserEntity();
            user.setId(UUID.randomUUID().toString());
            user.setName(name);
            user.setEmail(email);
            user.setAvatar(extractInitials(name));
            user.setReliability(5.0);
            user.setTripsCompleted(0);
            user.setTokenVersion(1);
            user = userRepository.save(user);
        } else {
            user = userOpt.get();
            // If the user's name is set to a placeholder, update to their real Google name
            if (name != null && !name.isEmpty() && !name.equals("Campus Student")) {
                if (user.getName() == null || user.getName().isEmpty() || "Test Student".equals(user.getName()) || "Aarav Sharma".equals(user.getName())) {
                    user.setName(name);
                    user.setAvatar(extractInitials(name));
                    user = userRepository.save(user);
                }
            }
        }

        if (Boolean.TRUE.equals(user.getDisabled())) {
            throw new UnauthorizedAccessException("This account is disabled.", 403);
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());
        setSessionCookie(response, token);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("token", token);
        result.put("user", serializerService.serializeUser(user));
        return result;
    }

    @Transactional
    public Map<String, Object> updatePassword(PasswordChangeRequest req, UserEntity user, HttpServletResponse response) {
        if (req.getPassword() == null || req.getPassword().trim().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            if (req.getCurrentPassword() == null || req.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("Current password is required to change your password.");
            }
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
                throw new IllegalArgumentException("Current password is incorrect.");
            }
        }

        user.setPasswordHash(passwordEncoder.encode(req.getPassword().trim()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        user = userRepository.save(user);

        String freshToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());
        setSessionCookie(response, freshToken);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", "Password saved.");
        result.put("token", freshToken);
        result.put("user", serializerService.serializeUser(user));
        return result;
    }

    @Transactional
    public Map<String, Object> updateProfile(UpdateProfileRequest req, UserEntity user) {
        if (req.getPhone() != null && !req.getPhone().trim().isEmpty()) {
            if (!req.getPhone().trim().matches("^\\+?[\\d ()-]{7,20}$")) {
                throw new IllegalArgumentException("Enter a valid contact phone number.");
            }
            user.setPhone(req.getPhone().trim());
        }

        if (req.getName() != null && !req.getName().trim().isEmpty()) {
            user.setName(req.getName().trim());
        }
        if (req.getHostel() != null) user.setHostel(req.getHostel().trim());
        if (req.getDegree() != null) user.setDegree(req.getDegree().trim());
        if (req.getYear() != null) user.setYear(req.getYear().trim());
        if (req.getHometown() != null) user.setHometown(req.getHometown().trim());
        if (req.getRollNumber() != null) user.setRollNumber(req.getRollNumber().trim());
        if (req.getPreferredTransport() != null) user.setPreferredTransport(req.getPreferredTransport().trim());
        if (req.getTimeWindow() != null) user.setTimeWindow(req.getTimeWindow().trim());
        if (req.getFrequentRoutes() != null) {
            user.setFrequentRoutes(String.join(",", req.getFrequentRoutes()));
        }
        if (req.getRideNotifications() != null) {
            user.setRideNotifications(req.getRideNotifications());
        }

        user = userRepository.save(user);

        // Synchronize updated profile to active group members
        List<GroupMemberEntity> activeMembers = groupMemberRepository.findByUserId(user.getId());
        for (GroupMemberEntity m : activeMembers) {
            if (user.getHostel() != null) m.setHostel(user.getHostel());
            if (user.getPhone() != null) m.setPhone(user.getPhone());
            if (user.getName() != null) m.setName(user.getName());
            groupMemberRepository.save(m);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("user", serializerService.serializeUser(user));
        return result;
    }

    @Transactional
    public Map<String, Object> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            try {
                String userId = jwtTokenProvider.getUserIdFromToken(token);
                Integer tokenVersion = jwtTokenProvider.getTokenVersion(token);
                Optional<UserEntity> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    UserEntity user = userOpt.get();
                    if (tokenVersion != null && tokenVersion.equals(user.getTokenVersion())) {
                        user.setTokenVersion(user.getTokenVersion() + 1);
                        userRepository.save(user);
                    }
                }
            } catch (Exception ignored) {}
        }

        clearSessionCookie(response);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", "Logged out successfully.");
        return result;
    }

    private static String extractInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "ST";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (sb.length() == 2) break;
            }
        }
        return sb.length() > 0 ? sb.toString() : "ST";
    }
}
