package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "hostels")
public class HostelEntity {

    @Id
private String id;
private String name;
private String gender;
private String cluster;

    public HostelEntity() {}

    public HostelEntity(String id, String name, String gender, String cluster) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.cluster = cluster;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCluster() { return cluster; }
    public void setCluster(String cluster) { this.cluster = cluster; }
}
