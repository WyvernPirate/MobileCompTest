package com.example.testmobilecomp;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String id;
    private String name;
    private String organizer;
    public int volunteerCount;
    private String status;
    private List<Volunteer> volunteers;

    public Task() {
    }

    public Task(String id, String name, String organizer, int volunteerCount, String status) {
        this.id = id;
        this.name = name;
        this.organizer = organizer;
        this.volunteerCount = volunteerCount;
        this.status = status;
        this.volunteers = new ArrayList<>();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public int getVolunteerCount() {
        return volunteerCount;
    }

    public void setVolunteerCount(int volunteerCount) {
        this.volunteerCount = volunteerCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

class Volunteer {
    private String name;
    private String preferredTime;

    public Volunteer() {}

    public Volunteer(String name, String preferredTime) {
        this.name = name;
        this.preferredTime = preferredTime;
    }

}

