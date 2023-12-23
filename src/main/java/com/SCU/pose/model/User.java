package com.SCU.pose.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExerciseDate> dates_exercise;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Video> videos; // List of videos associated with the user

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PushUp> pushups;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlutterKicks> flutterKicks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GluteBridge> gluteBridges;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SitUp> sitUps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Plank> planks;


    // Constructors
    public User() {
        // Default constructor
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters

    public List<ExerciseDate> getDates_exercise() {
        return dates_exercise;
    }

    public void setDates_exercise(List<ExerciseDate> dates_exercise) {
        this.dates_exercise = dates_exercise;
    }

    public List<PushUp> getPushups() {
        return pushups;
    }

    public void setPushups(List<PushUp> pushups) {
        this.pushups = pushups;
    }

    public List<FlutterKicks> getFlutterKicks() {
        return flutterKicks;
    }

    public void setFlutterKicks(List<FlutterKicks> flutterKicks) {
        this.flutterKicks = flutterKicks;
    }

    public List<GluteBridge> getGluteBridges() {
        return gluteBridges;
    }

    public void setGluteBridges(List<GluteBridge> gluteBridges) {
        this.gluteBridges = gluteBridges;
    }

    public List<SitUp> getSitUps() {
        return sitUps;
    }

    public void setSitUps(List<SitUp> sitUps) {
        this.sitUps = sitUps;
    }

    public List<Plank> getPlanks() {
        return planks;
    }

    public void setPlanks(List<Plank> planks) {
        this.planks = planks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}

