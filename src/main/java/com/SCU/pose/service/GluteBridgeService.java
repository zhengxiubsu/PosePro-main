package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.GluteBridgeRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GluteBridgeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GluteBridgeRepository gluteBridgeRepository;

    @Autowired
    private VideoRepository videoRepository;

    public void analyzeGluteBridges(int videoId, int userId) {
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            return; // Video not found
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return; // User not found
        }

        int gluteBridgeRepetitions = countGluteBridges(video);
        double averageScore = calculateAverageScore(video, gluteBridgeRepetitions);

        String performanceComment = generatePerformanceComment(gluteBridgeRepetitions, averageScore);

        GluteBridge gluteBridge = new GluteBridge(user, gluteBridgeRepetitions, averageScore, new Date(), video.getLabel(), performanceComment);
        gluteBridgeRepository.save(gluteBridge);
    }

    private int countGluteBridges(Video video) {
        int count = 0;
        boolean isUp = false;

        for (Image image : video.getImages()) {
            if (isGluteBridgeUp(image) && !isUp) {
                count++;
                isUp = true;
            } else if (!isGluteBridgeUp(image) && isUp) {
                isUp = false;
            }
        }

        return count;
    }

    private boolean isGluteBridgeUp(Image image) {
        // Placeholder logic for detecting the "up" position of a glute bridge
        // This should be replaced with actual logic based on your coordinate data
        Coordinate hip = getCoordinateByName(image, "Hip");
        Coordinate knee = getCoordinateByName(image, "Knee");

        if (hip == null || knee == null) {
            return false;
        }

        // Example condition: Hip is higher than knee in the "up" position
        return hip.getY() < knee.getY();
    }

    private double calculateAverageScore(Video video, int repetitions) {
        double totalScore = 0.0;
        for (Image image : video.getImages()) {
            totalScore += evaluateGluteBridgeForm(image);
        }

        return totalScore / video.getImages().size();
    }

    private double evaluateGluteBridgeForm(Image image) {
        // Placeholder logic for evaluating glute bridge form
        // This should be replaced with actual logic based on your coordinate data
        // Example: Good form if hip is aligned with shoulders and knees
        Coordinate hip = getCoordinateByName(image, "Hip");
        Coordinate shoulder = getCoordinateByName(image, "Shoulder");
        Coordinate knee = getCoordinateByName(image, "Knee");

        if (hip == null || shoulder == null || knee == null) {
            return 0; // Missing key coordinates
        }

        double alignmentScore = isProperlyAligned(hip, shoulder, knee) ? 10.0 : 5.0;
        return alignmentScore;
    }

    private boolean isProperlyAligned(Coordinate hip, Coordinate shoulder, Coordinate knee) {
        // Example alignment check
        return Math.abs(hip.getY() - ((shoulder.getY() + knee.getY()) / 2)) < 5; // Threshold can be adjusted
    }

    private Coordinate getCoordinateByName(Image image, String name) {
        return image.getCoordinates().stream()
                .filter(c -> c.getKeyPointName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private String generatePerformanceComment(int repetitions, double averageScore) {
        StringBuilder comment = new StringBuilder();
        comment.append("You performed ").append(repetitions).append(" glute bridges. ");

        if (averageScore >= 8) {
            comment.append("Excellent form! Keep up the good work.");
        } else if (averageScore >= 5) {
            comment.append("Good job, but there's room for improvement in your form.");
        } else {
            comment.append("Let's work on improving your form for better performance.");
        }

        return comment.toString();
    }
}
