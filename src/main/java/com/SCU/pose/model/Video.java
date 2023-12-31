package com.SCU.pose.model;

import javax.persistence.*;
import java.util.*;
import java.util.List;

@Entity
@Table(name = "Video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key in Video table
    private User user; // Reference back to the User entity

    @Column(name = "label")
    private String label;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id") // Foreign key in Image table
    private List<Image> images;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PushUp> pushUps;






    // Constructors
    public Video() {
    }

    public Video(List<Image> images, String label) {
        this.images = images;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PushUp> getPushUps() {
        return pushUps;
    }

    public void setPushUps(List<PushUp> pushUps) {
        this.pushUps = pushUps;
    }

}
