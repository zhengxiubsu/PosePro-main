package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.PushUpRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PushUpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PushUpRepository pushUpRepository;

    @Autowired
    private VideoRepository videoRepository;

    private boolean isDown = false; // Track the pushup position across frames


    public int analyzePushupFrame(Image image, int userId) {
        if (isPushupDown(image)) {
            if (!isDown) {
                isDown = true; // Mark as down
                return 0; // No count increment as it's the downward movement
            }
        } else {
            if (isDown) {
                isDown = false; // Reset for next pushup
                return updatePushUpCount(userId);
                // Increment count as user moved from down to up
            }
        }
        return 0; // Default return value when no pushup count increment
    }


    private boolean isPushupDown(Image image) {
        Coordinate rightElbow = getCoordinateByName(image, "Right Elbow");
        Coordinate rightShoulder = getCoordinateByName(image, "Right Shoulder");
        Coordinate leftElbow = getCoordinateByName(image, "Left Elbow");
        Coordinate leftShoulder = getCoordinateByName(image, "Left Shoulder");


        if (rightElbow == null || rightShoulder == null || leftElbow == null || leftShoulder == null) {
            return false;
        }

        boolean rightElbowDown = rightElbow.getY() > rightShoulder.getY();
        boolean leftElbowDown = leftElbow.getY() > leftShoulder.getY();

        return rightElbowDown && leftElbowDown;
    }




// 在 PushUpService 中

    public int updatePushUpCount(int userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        // Fetch the latest PushUp record for the given user
        Optional<PushUp> latestPushUpOpt = pushUpRepository.findTopByUserIdOrderByExerciseDateDesc(userId);

        // Use the latest record if it exists, otherwise create a new PushUp object
        PushUp latestPushUp = latestPushUpOpt.orElse(new PushUp(user, 0, 0.0, new Date(), "Standard Pushup", ""));

        // Increment the count of the latest or new PushUp record
        latestPushUp.setCount(latestPushUp.getCount() + 1);

        // Save the updated or new PushUp record to the database
        pushUpRepository.save(latestPushUp);
        return latestPushUp.getCount();

    }





























    public void analyzePushups(int videoId, int userId) {
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            return; // Video not found
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return; // User not found
        }

        int pushupCount = countPushups(video);
        double averageScore = calculateAverageScore(video, pushupCount);

        String performanceComment = generatePerformanceComment(pushupCount, averageScore);

        PushUp pushup = new PushUp(user, pushupCount, averageScore, new Date(), video.getLabel(), performanceComment);
        pushUpRepository.save(pushup);
    }

    private int countPushups(Video video) {
        int pushupCount = 0;
        boolean isDown = false;

        for (Image image : video.getImages()) {
            if (isPushupDown(image) && !isDown) {
                isDown = true;
            } else if (!isPushupDown(image) && isDown) {
                pushupCount++;
                isDown = false;
            }
        }

        return pushupCount;
    }



    private double calculateAverageScore(Video video, int pushupCount) {
        double totalScore = 0.0;
        int frameCount = video.getImages().size();

        for (Image image : video.getImages()) {
            double postureScore = evaluatePosture(image, pushupCount);
            double elbowAngleScore = evaluateElbowAngle(image, pushupCount);

            double frameScore = (postureScore + elbowAngleScore) / 2;
            totalScore += frameScore;
        }

        return frameCount > 0 ? totalScore / frameCount : 0.0;
    }

    private double evaluatePosture(Image image, int pushupCount) {
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

        double postureScoreAdjustmentFactor = 1.0 - (pushupCount / 20.0); // 20
        return Math.max(postureScore * postureScoreAdjustmentFactor, 0);
    }

    private double evaluateElbowAngle(Image image, int pushupCount) {
        Coordinate rightElbow = getCoordinateByName(image, "Right Elbow");
        Coordinate rightShoulder = getCoordinateByName(image, "Right Shoulder");
        Coordinate rightWrist = getCoordinateByName(image, "Right Wrist");

        if (rightElbow == null || rightShoulder == null || rightWrist == null) {
            return 0.0; // Missing key coordinates
        }

        double elbowAngle = calculateAngle(rightShoulder, rightElbow, rightWrist);
        double angleScore = elbowAngle <= 90 ? 10.0 : (180.0 - elbowAngle) / 9.0;

        double elbowAngleScoreAdjustmentFactor = 1.0 - (pushupCount / 20.0); // 假设超过20次开始影响分数
        return Math.max(angleScore * elbowAngleScoreAdjustmentFactor, 0);
    }

    private double calculateAngle(Coordinate a, Coordinate b, Coordinate c) {
        double abX = b.getX() - a.getX();
        double abY = b.getY() - a.getY();
        double bcX = c.getX() - b.getX();
        double bcY = c.getY() - b.getY();

        double dotProduct = abX * bcX + abY * bcY;
        double magnitudeAB = Math.sqrt(abX * abX + abY * abY);
        double magnitudeBC = Math.sqrt(bcX * bcX + bcY * bcY);

        double angle = Math.acos(dotProduct / (magnitudeAB * magnitudeBC));
        return Math.toDegrees(angle); // Convert to degrees
    }

    private Coordinate getCoordinateByName(Image image, String name) {
        return image.getCoordinates().stream()
                .filter(c -> c.getKeyPointName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private String generatePerformanceComment(int pushupCount, double averageScore) {
        StringBuilder comment = new StringBuilder();
        comment.append("You performed ").append(pushupCount).append(" pushups. ");

        if (averageScore >= 8) {
            comment.append("Excellent form and consistency! Keep up the good work.");
        } else if (averageScore >= 5) {
            comment.append("Good job, but there's room for improvement in your form.");
        } else {
            comment.append("Let's work on improving your form for better performance.");
        }

        return comment.toString();
    }

}

