package com.tiet.journeyconnect.dto;

public class SafetyDtos {

    public static class SafetyReportRequest {
        private String category;
        private String details;
        private String targetUserId;
        private String groupId;

        public SafetyReportRequest() {}

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
        public String getTargetUserId() { return targetUserId; }
        public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
    }

    public static class MuteUserRequest {
        private String userId;
        private Boolean muted;

        public MuteUserRequest() {}

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Boolean getMuted() { return muted; }
        public void setMuted(Boolean muted) { this.muted = muted; }
    }
}
