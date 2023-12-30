package com.SCU.pose.controller;

import com.SCU.pose.model.ExerciseRecord;
import com.SCU.pose.service.ExerciseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseRecordController {

    @Autowired
    private ExerciseRecordService exerciseRecordService;


    @GetMapping("/dates/{userId}")
    public ResponseEntity<List<Date>> getExerciseDates(
            @PathVariable int userId,
            @RequestParam String range) {
        List<Date> dates = exerciseRecordService.getExerciseDatesByUserAndRange(userId, range);
        return ResponseEntity.ok(dates);
    }


    @GetMapping("/scores/{userId}")
    public ResponseEntity<List<Double>> getExerciseScores(
            @PathVariable int userId,
            @RequestParam String exerciseType,
            @RequestParam String range) {
        List<Double> scores = exerciseRecordService.getExerciseScoresByUserTypeAndRange(userId, exerciseType, range);
        return ResponseEntity.ok(scores);
    }
}
