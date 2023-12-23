package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.SitUpRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SitUpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SitUpRepository sitUpRepository;

    @Autowired
    private VideoRepository videoRepository;

    public void analyzeSitUps(int videoId, int userId) {
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            return; // Video not found
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return; // User not found
        }

        int sitUpCount = countSitUps(video);
        double averageScore = calculateAverageScore(video);

        String performanceComment = generatePerformanceComment(sitUpCount, averageScore);

        SitUp sitUp = new SitUp(user, sitUpCount, averageScore, new Date(), video.getLabel(), performanceComment);
        sitUpRepository.save(sitUp);
    }

    private int countSitUps(Video video) {
        int sitUpCount = 0;
        boolean isUp = false;

        for (Image image : video.getImages()) {
            if (isSitUpUp(image) && !isUp) {
                isUp = true;
            } else if (!isSitUpUp(image) && isUp) {
                sitUpCount++;
                isUp = false;
            }
        }

        return sitUpCount;
    }

    private boolean isSitUpUp(Image image) {
        Coordinate head = getCoordinateByName(image, "Nose"); // Assuming nose as a reference for head position
        Coordinate hips = getCoordinateByName(image, "MidHip"); // Assuming mid-hip as a reference for hip position

        if (head == null || hips == null) {
            return false;
        }

        return head.getY() < hips.getY(); // In a sit-up, the head comes above the hips
    }

    private double calculateAverageScore(Video video) {
        double totalScore = 0.0;
        int frameCount = video.getImages().size();

        for (Image image : video.getImages()) {
            double postureScore = evaluatePosture(image);
            totalScore += postureScore;
        }

        return frameCount > 0 ? totalScore / frameCount : 0.0;
    }

    private double evaluatePosture(Image image) {
        Coordinate head = getCoordinateByName(image, "Nose");
        Coordinate hips = getCoordinateByName(image, "MidHip");

        if (head == null || hips == null) {
            return 0.0; // Missing key coordinates
        }

        double postureScore = 10 - Math.abs(head.getY() - hips.getY()); // Scale factor can be adjusted
        return Math.max(postureScore, 0); // Ensure score is not negative
    }

    private Coordinate getCoordinateByName(Image image, String name) {
        return image.getCoordinates().stream()
                .filter(c -> c.getKeyPointName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private String generatePerformanceComment(int sitUpCount, double averageScore) {
        StringBuilder comment = new StringBuilder();
        comment.append("You performed ").append(sitUpCount).append(" sit-ups. ");

        if (averageScore >= 8) {
            comment.append("Great form and effort! Keep it up.");
        } else if (averageScore >= 5) {
            comment.append("Good effort, but there's room to improve your form.");
        } else {
            comment.append("Focus on improving your form for a better workout.");
        }

        return comment.toString();
    }
}
