package com.SCU.pose.model;

import javax.persistence.*;

import java.util.*;


@Entity
@Table(name = "pushups")
public class PushUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "count")
    private int count;

    @Column(name = "average_score")
    private double averageScore;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exercise_date")
    private Date exerciseDate;

    @Column(name = "label")
    private String label; // Label for the exercise, e.g., "Standard Pushup", "Wide Grip Pushup"

    @Column(name = "comment", length = 1000) // Assuming comments won't be too long
    private String comment; // Any additional comments about the exercise session

    // Constructors
    public PushUp() {
        // Default constructor
    }

    public PushUp(User user, int count, double averageScore, Date exerciseDate, String label, String comment) {
        this.user = user;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
        return "PushUp{" +
                "id=" + id +
                ", user=" + user +
                ", count=" + count +
                ", averageScore=" + averageScore +
                ", exerciseDate=" + exerciseDate +
                ", label='" + label + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
