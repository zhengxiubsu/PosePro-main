package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.PlankRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PlankService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlankRepository plankRepository;

    @Autowired
    private VideoRepository videoRepository;

    /**
     * Analyzes plank exercises from a video and saves the analysis to the associated user.
     *
     * @param videoId The ID of the video containing plank exercises.
     * @param userId The ID of the user performing the plank exercises.
     */
    public void analyzePlank(int videoId, int userId) {
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            // Handle video not found
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // Handle user not found
            return;
        }

        int duration = calculatePlankDuration(video); // Calculate the duration of the plank
        double score = evaluatePlank(video, duration); // Evaluate the form of the plank with duration
        String performanceComment = generatePlankPerformanceComment(score);

        Plank plank = new Plank(user, duration, score, new Date(), video.getLabel(), performanceComment);
        plankRepository.save(plank);
    }

    private int calculatePlankDuration(Video video) {
        // Implement the logic for calculating the duration of the plank
        // This is a placeholder implementation
        return 60; // Temporary duration in seconds
    }

    private double evaluatePlank(Video video, int duration) {
        double totalScore = 0.0;
        int frameCount = video.getImages().size();

        for (Image image : video.getImages()) {
            double postureScore = evaluatePlankPosture(image);
            totalScore += postureScore;
        }

        double averagePostureScore = frameCount > 0 ? totalScore / frameCount : 0.0;

        // Adjust score based on duration (you can define your own adjustment criteria)
        double adjustedScore = adjustScoreBasedOnDuration(averagePostureScore, duration);

        return adjustedScore;
    }

    private double evaluatePlankPosture(Image image) {
        // Implementation for evaluating plank posture
        // This should be replaced with actual logic based on your coordinate data
        Coordinate leftShoulder = getCoordinateByName(image, "Left Shoulder");
        Coordinate rightShoulder = getCoordinateByName(image, "Right Shoulder");
        Coordinate leftHip = getCoordinateByName(image, "Left Hip");
        Coordinate rightHip = getCoordinateByName(image, "Right Hip");

        if (leftShoulder == null || rightShoulder == null || leftHip == null || rightHip == null) {
            return 0.0; // Missing key coordinates
        }

        double shoulderY = (leftShoulder.getY() + rightShoulder.getY()) / 2;
        double hipY = (leftHip.getY() + rightHip.getY()) / 2;

        double postureDeviation = Math.abs(shoulderY - hipY); // Deviation from straight line
        double postureScore = 10 - (postureDeviation * 2); // Scale factor can be adjusted

        return Math.max(postureScore, 0); // Ensure score is not negative
    }

    private String generatePlankPerformanceComment(double averageScore) {
        if (averageScore >= 8) {
            return "Excellent plank form and consistency! Keep up the good work.";
        } else if (averageScore >= 5) {
            return "Good job, but there's room for improvement in maintaining a straight posture.";
        } else {
            return "Let's work on improving your plank posture for better performance.";
        }
    }

    private Coordinate getCoordinateByName(Image image, String name) {
        return image.getCoordinates().stream()
                .filter(c -> c.getKeyPointName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private double adjustScoreBasedOnDuration(double averagePostureScore, int duration) {
        // Example adjustment: Increase score for longer durations
        if (duration > 60) {
            return averagePostureScore + 2.0;
        } else if (duration > 30) {
            return averagePostureScore + 1.0;
        }
        return averagePostureScore;
    }
}
