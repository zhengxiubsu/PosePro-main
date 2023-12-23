package com.SCU.pose.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "situps")
public class SitUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "repetitions")
    private int repetitions; // Number of sit-up repetitions

    @Column(name = "average_score")
    private double averageScore; // Average score for the form during the exercise

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exercise_date")
    private Date exerciseDate; // The date and time when the exercise was performed

    @Column(name = "label")
    private String label; // Label for the exercise type, e.g., "Standard SitUp", "Twist SitUp"

    @Column(name = "comment", length = 1000) // Assuming comments won't be too long
    private String comment; // Additional comments or notes about the exercise session

    // Constructors
    public SitUp() {
        // Default constructor
    }

    public SitUp(User user, int repetitions, double averageScore, Date exerciseDate, String label, String comment) {
        this.user = user;
        this.repetitions = repetitions;
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

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
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
        return "SitUp{" +
                "id=" + id +
                ", user=" + user +
                ", repetitions=" + repetitions +
                ", averageScore=" + averageScore +
                ", exerciseDate=" + exerciseDate +
                ", label='" + label + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
