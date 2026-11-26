package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.dto.SafetyDtos.*;
import com.tiet.journeyconnect.exceptions.UnauthorizedAccessException;
import com.tiet.journeyconnect.models.entities.SafetyReportEntity;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.repositories.SafetyReportRepository;
import com.tiet.journeyconnect.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class SafetyService {

    @Autowired
    private SafetyReportRepository safetyReportRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Map<String, Object> muteUser(MuteUserRequest req, UserEntity user) {
        if (req.getUserId() == null || req.getUserId().equals(user.getId()) || req.getMuted() == null) {
            throw new IllegalArgumentException("Choose another user and a mute setting.");
        }

        if (!userRepository.existsById(req.getUserId())) {
            throw new IllegalArgumentException("User not found.");
        }

        Set<String> muted = new HashSet<>();
        if (user.getMutedUsers() != null && !user.getMutedUsers().trim().isEmpty()) {
            muted.addAll(Arrays.asList(user.getMutedUsers().split(",")));
        }

        if (Boolean.TRUE.equals(req.getMuted())) {
            muted.add(req.getUserId());
        } else {
            muted.remove(req.getUserId());
        }

        user.setMutedUsers(String.join(",", muted));
        userRepository.save(user);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        return res;
    }

    @Transactional
    public Map<String, Object> submitReport(SafetyReportRequest req, UserEntity user) {
        if (req.getCategory() == null || req.getDetails() == null || req.getDetails().trim().isEmpty()) {
            throw new IllegalArgumentException("Choose a category and provide detail.");
        }

        SafetyReportEntity report = new SafetyReportEntity();
        report.setId(UUID.randomUUID().toString());
        report.setReporterId(user.getId());
        report.setCategory(req.getCategory().trim());
        report.setDetails(req.getDetails().trim());
        report.setTargetUserId(req.getTargetUserId() != null ? req.getTargetUserId() : "");
        report.setGroupId(req.getGroupId() != null ? req.getGroupId() : "");
        report.setStatus("Open");
        report.setCreatedAt(Instant.now());
        report = safetyReportRepository.save(report);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", report.getId());
        res.put("status", report.getStatus());
        return res;
    }

    public List<SafetyReportEntity> getReports(UserEntity user) {
        if ("admin".equalsIgnoreCase(user.getRole())) {
            return safetyReportRepository.findAllByOrderByCreatedAtDesc();
        }
        return safetyReportRepository.findByReporterIdOrderByCreatedAtDesc(user.getId());
    }

    @Transactional
    public SafetyReportEntity reviewReport(String id, UserEntity user) {
        if (!"admin".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedAccessException("Administrator access required.", 403);
        }

        Optional<SafetyReportEntity> reportOpt = safetyReportRepository.findById(id);
        if (reportOpt.isEmpty()) {
            throw new IllegalArgumentException("Report not found.");
        }

        SafetyReportEntity report = reportOpt.get();
        report.setStatus("Reviewed");
        return safetyReportRepository.save(report);
    }
}
