package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.PushUpRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PushUpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PushUpRepository pushUpRepository;

    @Autowired
    private VideoRepository videoRepository;

    private boolean isDown = false; // Track the pushup position across frames



    //**********************************12.31**********************************
    public int analyzePushupFrame(Image image, int userId, int videoId) {
        int currentCount = getCurrentPushUpCount(videoId);
        if (isPushupDown(image)) {
            if (!isDown) {
                isDown = true;
                return currentCount;
            }
        } else {
            if (isDown) {
                isDown = false;
                return updatePushUpCount(userId, videoId);
            }
        }
        return currentCount;
    }




    //**********************************12.31**********************************
    public int updatePushUpCount(int userId, int videoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        Optional<PushUp> latestPushUpOpt = pushUpRepository.findTopByVideoIdOrderByExerciseDateDesc(videoId);

        PushUp latestPushUp = latestPushUpOpt
                .orElse(new PushUp(user, 0, 0.0, new Date(), "Standard Pushup", "", video));

        latestPushUp.setCount(latestPushUp.getCount() + 1);

        pushUpRepository.save(latestPushUp);
        return latestPushUp.getCount();
    }


    //**********************************12.31**********************************
    public int getCurrentPushUpCount(int videoId) {

        Optional<PushUp> latestPushUpOpt = pushUpRepository.findTopByVideoIdOrderByExerciseDateDesc(videoId);
        return latestPushUpOpt.map(PushUp::getCount).orElse(0);
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





    public List<Map<Date, Integer>> getOrderedPushUpsByUser(int userId) {
        List<PushUp> pushUps = pushUpRepository.findByUserIdOrderByExerciseDateAscVideoIdAsc(userId);

        Map<Date, Map<Integer, Integer>> dateVideoCountMap = new LinkedHashMap<>();
        for (PushUp pushUp : pushUps) {
            Date date = pushUp.getExerciseDate();
            Integer videoId = pushUp.getVideo().getId();
            Integer count = pushUp.getCount();

            dateVideoCountMap.computeIfAbsent(date, k -> new HashMap<>())
                    .merge(videoId, count, Integer::sum);
        }


        List<Map<Date, Integer>> resultList = new ArrayList<>();
        dateVideoCountMap.forEach((date, videoCountMap) -> {
            for (Map.Entry<Integer, Integer> entry : videoCountMap.entrySet()) {
                Map<Date, Integer> map = new HashMap<>();
                map.put(date, entry.getValue());
                resultList.add(map);
            }
        });

        return resultList;
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

