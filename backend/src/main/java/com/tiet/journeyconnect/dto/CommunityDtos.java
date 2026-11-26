package com.tiet.journeyconnect.dto;

import java.util.List;

public class CommunityDtos {

    public static class CreateCommunityRequest {
        private String name;
        private String state;
        private String icon;
        private String distanceFromThapar;
        private String popularTransport;
        private String description;
        private List<String> tags;

        public CreateCommunityRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getDistanceFromThapar() { return distanceFromThapar; }
        public void setDistanceFromThapar(String distanceFromThapar) { this.distanceFromThapar = distanceFromThapar; }
        public String getPopularTransport() { return popularTransport; }
        public void setPopularTransport(String popularTransport) { this.popularTransport = popularTransport; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }

    public static class CreatePostRequest {
        private String title;
        private String content;
        private String category;

        public CreatePostRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class AddCommentRequest {
        private String text;

        public AddCommentRequest() {}

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
