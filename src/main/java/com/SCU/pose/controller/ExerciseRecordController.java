package com.SCU.pose.controller;

import com.SCU.pose.model.ExerciseRecord;
import com.SCU.pose.service.ExerciseRecordService;
import com.SCU.pose.service.PushUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exercises")
public class ExerciseRecordController {

    @Autowired
    private ExerciseRecordService exerciseRecordService;

    @Autowired
    private PushUpService pushUpService;

    @GetMapping("/record-count/{userId}")
    public ResponseEntity<List<Map<Date, Integer>>> getPushUpsByUser(@PathVariable int userId) {
        List<Map<Date, Integer>> pushUps = pushUpService.getOrderedPushUpsByUser(userId);
        return ResponseEntity.ok(pushUps);
    }







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
