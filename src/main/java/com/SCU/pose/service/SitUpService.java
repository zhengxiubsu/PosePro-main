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






    private boolean isUpPreviously = false;  // This flag tracks if the user was in the "up" position in the previous frame

    /**
     * Analyzes a single frame to determine if it represents a sit-up movement.
     *
     * @param image The image object representing a single frame of the video.
     * @return Returns 1 if a sit-up is detected, otherwise returns 0.
     */
    public int analyzeSitUpFrame(Image image) {
        boolean isCurrentlyUp = isSitUpUp(image);  // Check if the user is in the "up" position in the current frame

        if (isCurrentlyUp && !isUpPreviously) {
            // If the user is currently "up" and was not "up" in the previous frame, it's counted as one complete sit-up
            isUpPreviously = true;  // Update the flag as the user is now "up"
            return 1;  // Count this frame as one sit-up
        } else if (!isCurrentlyUp && isUpPreviously) {
            // If the user was "up" previously but not in the current frame, reset the flag
            isUpPreviously = false;  // Reset the flag as the user is no longer "up"
        }

        return 0;  // No new sit-up detected in this frame
    }

    /**
     * Determines if the user is in the "up" position of a sit-up.
     *
     * @param image The image object to analyze.
     * @return Returns true if the user is in the "up" position, otherwise false.
     */
    private boolean isSitUpUp(Image image) {
        Coordinate head = getCoordinateByName(image, "Nose"); // Assuming nose as a reference for head position
        Coordinate hips = getCoordinateByName(image, "MidHip"); // Assuming mid-hip as a reference for hip position

        if (head == null || hips == null) {
            return false;  // Return false if essential coordinates are missing
        }

        return head.getY() < hips.getY(); // In a sit-up, the head should be above the hips for the "up" position
    }




































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
