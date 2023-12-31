package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.util.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PushUpRepository pushUpRepository;
    @Autowired
    private PlankRepository plankRepository;
    @Autowired
    private FlutterKicksRepository flutterKickRepository;
    @Autowired
    private GluteBridgeRepository gluteBridgeRepository;
    @Autowired
    private SitUpRepository sitUpRepository;

    // Create a new user
    public User createUser(User user) {
        // Save the new user to the database
        return userRepository.save(user);
    }

    // Get a user by their ID
    public Optional<User> getUserById(Integer id) {
        // Find the user by ID
        return userRepository.findById(id);
    }

    // Get all users
    public List<User> getAllUsers() {
        // Retrieve all users from the database
        return userRepository.findAll();
    }

    // Update a user
    public User updateUser(User user) {
        // Save the updated user information
        return userRepository.save(user);
    }

    // Delete a user
    public void deleteUser(Integer id) {
        // Delete the user by ID
        userRepository.deleteById(id);
    }

    // Get a user's exercise dates
    public List<Date> getExerciseDatesForUser(int userId) {
        // Retrieve exercise dates for a specific user
        return userRepository.findById(userId)
                .map(User::getDates_exercise)
                .orElse(Collections.emptyList())
                .stream()
                .map(ExerciseDate::getDate)
                .collect(Collectors.toList());
    }

    // Update a user's exercise date
    public boolean updateExerciseDateForUser(int userId, Date exerciseDate) {
        // Update the exercise date for a specific user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            ExerciseDate newExerciseDate = new ExerciseDate(user, exerciseDate, "Exercise Label"); // Specify the correct label
            user.getDates_exercise().add(newExerciseDate);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Verify a user's password
    public boolean verifyPassword(int userId, String password) {
        // Verify the user's password (Note: Use encryption in production)
        return userRepository.findById(userId)
                .map(User::getPassword)
                .map(storedPassword -> storedPassword.equals(password)) // Encryption should be used here
                .orElse(false);
    }

    // Get analytics for Push-Up exercises
    public JSONObject getPushUpAnalytics(int userId) {
        List<PushUp> pushUps = pushUpRepository.findByUserId(userId);
        return analyzePushUps(pushUps);
    }

    // Get analytics for Plank exercises
    public JSONObject getPlankAnalytics(int userId) {
        List<Plank> planks = plankRepository.findByUserId(userId);
        return analyzePlanks(planks);
    }

    // Get analytics for Flutter Kick exercises
//    public JSONObject getFlutterKickAnalytics(int userId) {
//        List<FlutterKicks> flutterKicks = flutterKickRepository.findByUserId(userId);
//        return analyzeFlutterKicks(flutterKicks);
//    }

    // Get analytics for Glute Bridge exercises
    public JSONObject getGluteBridgeAnalytics(int userId) {
        List<GluteBridge> gluteBridges = gluteBridgeRepository.findByUserId(userId);
        return analyzeGluteBridges(gluteBridges);
    }

    // Get analytics for Sit-Up exercises
//    public JSONObject getSitUpAnalytics(int userId) {
//        List<SitUp> sitUps = sitUpRepository.findByUserId(userId);
//        return analyzeSitUps(sitUps);
//    }

    private JSONObject analyzePushUps(List<PushUp> pushUps) {
        List<Double> scores = pushUps.stream().map(PushUp::getAverageScore).collect(Collectors.toList());
        return createAnalysisJSONObject(scores);
    }


    private JSONObject analyzePlanks(List<Plank> planks) {
        // Convert the list of Plank entities to a list of Double,
        // representing the duration of each Plank exercise in double format.
        // This conversion is necessary because createAnalysisJSONObject expects a List<Double>.
        List<Double> durationsAsDouble = planks.stream()
                .mapToInt(Plank::getDuration) // Convert Integer to primitive int
                .asDoubleStream() // Convert int stream to double stream
                .boxed() // Box the primitive double stream to Double object stream
                .collect(Collectors.toList()); // Collect the results into a List<Double>

        // Call createAnalysisJSONObject with the list of durations to perform the analysis
        return createAnalysisJSONObject(durationsAsDouble);
    }


//    private JSONObject analyzeFlutterKicks(List<FlutterKicks> flutterKicks) {
//        // Convert the list of FlutterKicks entities to a list of Double,
//        // representing the duration of each FlutterKick exercise in double format.
//        List<Double> durationsAsDouble = flutterKicks.stream()
//                .mapToInt(FlutterKicks::getDuration) // Convert Integer to primitive int
//                .asDoubleStream() // Convert int stream to double stream
//                .boxed() // Box the primitive double stream to Double object stream
//                .collect(Collectors.toList()); // Collect the results into a List<Double>
//
//        // Call createAnalysisJSONObject with the list of durations to perform the analysis
//        return createAnalysisJSONObject(durationsAsDouble);
//    }

    private JSONObject analyzeGluteBridges(List<GluteBridge> gluteBridges) {
        List<Double> repetitionsAsDouble = gluteBridges.stream()
                .mapToInt(GluteBridge::getRepetitions)
                .asDoubleStream() //
                .boxed() //
                .collect(Collectors.toList()); //

        return createAnalysisJSONObject(repetitionsAsDouble);
    }


//    private JSONObject analyzeSitUps(List<SitUp> sitUps) {
//        // Implementation details for sit-up analysis
//        return createAnalysisJSONObject(sitUps.stream().map(SitUp::getRepetitions).collect(Collectors.toList()));
//    }

    private JSONObject createAnalysisJSONObject(List<Double> values) {
        double average = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double stdDeviation = calculateStandardDeviation(values, average);

        JSONObject result = new JSONObject();
        result.put("average", average);
        result.put("max", max);
        result.put("stdDeviation", stdDeviation);

        return result;
    }

    private double calculateStandardDeviation(List<Double> values, double mean) {
        double sum = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum();
        return Math.sqrt(sum / values.size());
    }
}
