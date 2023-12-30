package com.SCU.pose.service;

import com.SCU.pose.model.ExerciseRecord;
import com.SCU.pose.repository.ExerciseRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseRecordService {

    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;

    public void addExerciseRecord(ExerciseRecord exerciseRecord) {
        exerciseRecordRepository.save(exerciseRecord);
    }

    public List<Date> getExerciseDatesByUserAndRange(int userId, String range) {
        Date startDate = getStartDateForRange(range);
        Date endDate = new Date();
        return exerciseRecordRepository.findByUserIdAndExerciseDateBetweenOrderByExerciseDateAsc(userId, startDate, endDate)
                .stream()
                .map(ExerciseRecord::getExerciseDate)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Double> getExerciseScoresByUserTypeAndRange(int userId, String exerciseType, String range) {
        Date startDate = getStartDateForRange(range);
        Date endDate = new Date();
        return exerciseRecordRepository.findByUserIdAndExerciseTypeAndExerciseDateBetweenOrderByExerciseDateAsc(userId, exerciseType, startDate, endDate)
                .stream()
                .map(ExerciseRecord::getScore)
                .collect(Collectors.toList());
    }

    private Date getStartDateForRange(String range) {
        LocalDateTime now = LocalDateTime.now();
        switch (range) {
            case "week":
                return Date.from(now.minusWeeks(1).atZone(ZoneId.systemDefault()).toInstant());
            case "month":
                return Date.from(now.minusMonths(1).atZone(ZoneId.systemDefault()).toInstant());
            case "year":
                return Date.from(now.minusYears(1).atZone(ZoneId.systemDefault()).toInstant());
            default:
                return new Date(); // Current day
        }
    }
}
