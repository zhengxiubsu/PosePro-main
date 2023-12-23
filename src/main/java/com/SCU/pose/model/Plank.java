package com.SCU.pose.model;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name = "planks")
public class Plank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "duration") // Duration of the plank in seconds
    private int duration;

    @Column(name = "average_score")
    private double averageScore;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exercise_date")
    private Date exerciseDate;

    @Column(name = "label")
    private String label; // Label for the exercise, e.g., "Forearm Plank", "Side Plank"

    @Column(name = "comment", length = 1000) // Assuming comments won't be too long
    private String comment; // Any additional comments about the exercise session

    // Constructors
    public Plank() {
        // Default constructor
    }

    public Plank(User user, int duration, double averageScore, Date exerciseDate, String label, String comment) {
        this.user = user;
        this.duration = duration;
        this.averageScore = averageScore;
        this.exerciseDate = exerciseDate;
        this.label = label;
        this.comment = comment;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public Date getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(Date exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // toString method for easy printing of the object, if needed
    @Override
    public String toString() {
        return "Plank{" +
                "id=" + id +
                ", user=" + user +
                ", duration=" + duration +
                ", averageScore=" + averageScore +
                ", exerciseDate=" + exerciseDate +
                ", label='" + label + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
