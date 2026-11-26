package com.tiet.journeyconnect.dto;

import java.util.List;

public class AuthDtos {

    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class GoogleAuthRequest {
        private String credential;

        public GoogleAuthRequest() {}

        public String getCredential() { return credential; }
        public void setCredential(String credential) { this.credential = credential; }
    }

    public static class PasswordChangeRequest {
        private String password;
        private String currentPassword;

        public PasswordChangeRequest() {}

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    }

    public static class UpdateProfileRequest {
        private String name;
        private String hostel;
        private String phone;
        private String degree;
        private String year;
        private String hometown;
        private String rollNumber;
        private String preferredTransport;
        private String timeWindow;
        private List<String> frequentRoutes;
        private Boolean rideNotifications;

        public UpdateProfileRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getHostel() { return hostel; }
        public void setHostel(String hostel) { this.hostel = hostel; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }
        public String getHometown() { return hometown; }
        public void setHometown(String hometown) { this.hometown = hometown; }
        public String getRollNumber() { return rollNumber; }
        public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
        public String getPreferredTransport() { return preferredTransport; }
        public void setPreferredTransport(String preferredTransport) { this.preferredTransport = preferredTransport; }
        public String getTimeWindow() { return timeWindow; }
        public void setTimeWindow(String timeWindow) { this.timeWindow = timeWindow; }
        public List<String> getFrequentRoutes() { return frequentRoutes; }
        public void setFrequentRoutes(List<String> frequentRoutes) { this.frequentRoutes = frequentRoutes; }
        public Boolean getRideNotifications() { return rideNotifications; }
        public void setRideNotifications(Boolean rideNotifications) { this.rideNotifications = rideNotifications; }
    }
}
