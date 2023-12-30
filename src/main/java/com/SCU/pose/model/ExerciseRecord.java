package com.SCU.pose.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "exercise_records")
public class ExerciseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who performed the exercise

    @Column(name = "exercise_date")
    @Temporal(TemporalType.DATE)
    private Date exerciseDate; // The date when the exercise was performed

    @Column(name = "score")
    private double score; // The score or performance indicator for the exercise

    @Column(name = "exercise_type")
    private String exerciseType; // The type of exercise, e.g., "push_up", "plank", etc.

    // Default constructor
    public ExerciseRecord() {
    }

    // Constructor with parameters
    public ExerciseRecord(User user, Date exerciseDate, double score, String exerciseType) {
        this.user = user;
        this.exerciseDate = exerciseDate;
        this.score = score;
        this.exerciseType = exerciseType;
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

    public Date getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(Date exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    // toString method for easy representation of the object
    @Override
    public String toString() {
        return "ExerciseRecord{" +
                "id=" + id +
                ", user=" + user +
                ", exerciseDate=" + exerciseDate +
                ", score=" + score +
                ", exerciseType='" + exerciseType + '\'' +
                '}';
    }
}
