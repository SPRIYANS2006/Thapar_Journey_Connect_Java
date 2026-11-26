package com.tiet.journeyconnect.controllers;

import com.tiet.journeyconnect.models.entities.DestinationEntity;
import com.tiet.journeyconnect.models.entities.HostelEntity;
import com.tiet.journeyconnect.repositories.CommunityRepository;
import com.tiet.journeyconnect.repositories.DestinationRepository;
import com.tiet.journeyconnect.repositories.GroupRepository;
import com.tiet.journeyconnect.repositories.HostelRepository;
import com.tiet.journeyconnect.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MetaController {

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Value("${app.google.client-id:629250761631-k5eo83notfq2e1vuu3qvimbf1jq3lueo.apps.googleusercontent.com}")
    private String googleClientId;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "ok");
        body.put("database", "connected");
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("googleClientId", googleClientId);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/destinations")
    public ResponseEntity<List<Map<String, Object>>> getDestinations() {
        List<DestinationEntity> list = destinationRepository.findAllByOrderByIdAsc();
        List<Map<String, Object>> result = new ArrayList<>();
        for (DestinationEntity d : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", d.getId());
            map.put("name", d.getName());
            map.put("icon", d.getIcon());
            map.put("description", d.getDescription());
            map.put("popularTime", d.getPopularTime());
            map.put("distance", d.getDistance());
            map.put("avgCabFare", d.getAvgCabFare());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/hostels")
    public ResponseEntity<List<Map<String, Object>>> getHostels() {
        List<HostelEntity> list = hostelRepository.findAllByOrderByClusterAscNameAsc();
        List<Map<String, Object>> result = new ArrayList<>();
        for (HostelEntity h : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", h.getId());
            map.put("name", h.getName());
            map.put("gender", h.getGender());
            map.put("cluster", h.getCluster());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long totalUsers = userRepository.count();
        long activeGroups = groupRepository.countByStatusIn(List.of("Open", "Confirmed"));
        long totalCommunities = communityRepository.count();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("verifiedStudentsCount", totalUsers);
        body.put("activeRidesCount", activeGroups);
        body.put("communitiesCount", totalCommunities);
        body.put("carbonSavedKg", null);
        return ResponseEntity.ok(body);
    }

    // Deprecated endpoints explicitly returning 404 (Luggage and Vendor Dashboard are permanently removed)
    @RequestMapping({"/luggage", "/luggage/**", "/vendor", "/vendor/**"})
    public ResponseEntity<Map<String, Object>> handleDeprecatedEndpoints() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "This feature has been permanently retired and removed from the active transit platform.");
        body.put("status", 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
