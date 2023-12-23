package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.FlutterKicksRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FlutterKicksService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlutterKicksRepository flutterKicksRepository;

    @Autowired
    private VideoRepository videoRepository;

    public void analyzeFlutterKicks(int videoId, int userId) {
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            return; // Video not found
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return; // User not found
        }

        int durationSeconds = calculateFlutterKicksDuration(video);
        double score = evaluateFlutterKicks(video);

        FlutterKicks flutterKicks = new FlutterKicks(user, durationSeconds, score, new Date(), video.getLabel(), generatePerformanceComment(score));
        flutterKicksRepository.save(flutterKicks);
    }

    private int calculateFlutterKicksDuration(Video video) {
        // Placeholder logic for calculating the duration of flutter kick exercises
        // This should be replaced with actual logic based on your video data
        // For example, duration could be calculated based on the number of frames or timestamps
        return video.getImages().size() * 2; // Assuming 2 seconds per frame as an example
    }

    private double evaluateFlutterKicks(Video video) {
        double totalScore = 0.0;
        for (Image image : video.getImages()) {
            totalScore += evaluateFlutterKickForm(image);
        }

        return totalScore / video.getImages().size();
    }

    private double evaluateFlutterKickForm(Image image) {
        // Placeholder logic for evaluating flutter kick form
        // This should be replaced with actual logic based on your coordinate data
        // Example: Good form if legs are properly lifted and straight
        Coordinate leftLeg = getCoordinateByName(image, "Left Leg");
        Coordinate rightLeg = getCoordinateByName(image, "Right Leg");

        if (leftLeg == null || rightLeg == null) {
            return 0; // Missing key coordinates
        }

        // Example condition for good form
        return (leftLeg.getY() < image.getCenterY() && rightLeg.getY() < image.getCenterY()) ? 10.0 : 5.0;
    }

    private Coordinate getCoordinateByName(Image image, String name) {
        return image.getCoordinates().stream()
                .filter(c -> c.getKeyPointName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private String generatePerformanceComment(double averageScore) {
        if (averageScore >= 8) {
            return "Excellent flutter kick form! Keep up the good work.";
        } else if (averageScore >= 5) {
            return "Good job, but there's room for improvement in maintaining proper leg position.";
        } else {
            return "Let's work on improving your flutter kick form for better performance.";
        }
    }
}
