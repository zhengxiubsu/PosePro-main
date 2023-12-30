package com.SCU.pose.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id") // Foreign key in Coordinate table
    private List<Coordinate> coordinates;

    // Constructors
    public Image() {
    }

    public Image(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Calculates the average Y-coordinate of all coordinates in the image.
     *
     * @return The average Y-coordinate.
     */
    public double getCenterY() {
        if (coordinates == null || coordinates.isEmpty()) {
            return 0;
        }

        double totalY = 0;
        for (Coordinate coord : coordinates) {
            totalY += coord.getY();
        }

        return totalY / coordinates.size();
    }

}
