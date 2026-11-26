package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hostels")
public class HostelEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "cluster", nullable = false)
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
