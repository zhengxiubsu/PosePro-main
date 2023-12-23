package com.SCU.pose.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents an exercise date for a user.
 * It tracks when a user has performed a specific exercise.
 */
@Entity
@Table(name = "exercise_dates")
public class ExerciseDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user associated with this exercise date

    @Column(name = "exercise_date")
    @Temporal(TemporalType.DATE)
    private Date date; // The date of the exercise

    @Column(name = "label")
    private String label; // Label for the exercise (e.g., "Running", "Push-up", "Yoga")

    /**
     * Default constructor.
     */
    public ExerciseDate() {
    }

    /**
     * Constructor with parameters.
     *
     * @param user  The user associated with this exercise date.
     * @param date  The date of the exercise.
     * @param label The label for the exercise.
     */
    public ExerciseDate(User user, Date date, String label) {
        this.user = user;
        this.date = date;
        this.label = label;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Overridden toString method for easy printing of the object.
     */
    @Override
    public String toString() {
        return "ExerciseDate{" +
                "id=" + id +
                ", user=" + user +
                ", date=" + date +
                ", label='" + label + '\'' +
                '}';
    }
}
