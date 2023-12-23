package com.SCU.pose.controller;

import com.SCU.pose.model.User;
import com.SCU.pose.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Create a new user
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // Get a user by their ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all users
    @GetMapping("/")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Update a user
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete a user
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // Get a user's exercise dates
    @GetMapping("/{userId}/exercise-dates")
    public ResponseEntity<List<Date>> getUserExerciseDates(@PathVariable int userId) {
        List<Date> exerciseDates = userService.getExerciseDatesForUser(userId);
        return ResponseEntity.ok(exerciseDates);
    }

    // Update a user's exercise date
    @PutMapping("/{userId}/exercise-dates")
    public ResponseEntity<Void> updateUserExerciseDate(@PathVariable int userId, @RequestBody Date exerciseDate) {
        boolean isUpdated = userService.updateExerciseDateForUser(userId, exerciseDate);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Verify a user's password
    @PostMapping("/{userId}/verify-password")
    public ResponseEntity<Boolean> verifyUserPassword(@PathVariable int userId, @RequestBody String password) {
        boolean isPasswordValid = userService.verifyPassword(userId, password);
        return ResponseEntity.ok(isPasswordValid);
    }

    // Get analytics for plank exercises
    @GetMapping("/{userId}/plank-analytics")
    public ResponseEntity<?> getPlankAnalytics(@PathVariable int userId) {
        // Implementation depends on the specific logic and data structure
        return ResponseEntity.ok("Logic to return plank analytics for the user");
    }

    // Get analytics for push-up exercises
    @GetMapping("/{userId}/pushup-analytics")
    public ResponseEntity<?> getPushUpAnalytics(@PathVariable int userId) {
        // Implementation depends on the specific logic and data structure
        return ResponseEntity.ok("Logic to return push-up analytics for the user" );
    }

    // Get analytics for flutter kick exercises
    @GetMapping("/{userId}/flutter-kick-analytics")
    public ResponseEntity<?> getFlutterKickAnalytics(@PathVariable int userId) {
        // Placeholder for flutter kick analytics logic
        return ResponseEntity.ok("Logic to return flutter kick analytics for the user");
    }

    // Get analytics for glute bridge exercises
    @GetMapping("/{userId}/glute-bridge-analytics")
    public ResponseEntity<?> getGluteBridgeAnalytics(@PathVariable int userId) {
        // Placeholder for glute bridge analytics logic
        return ResponseEntity.ok("Logic to return glute bridge analytics for the user");
    }

    // Get analytics for sit-up exercises
    @GetMapping("/{userId}/sit-up-analytics")
    public ResponseEntity<?> getSitUpAnalytics(@PathVariable int userId) {
        // Placeholder for sit-up analytics logic
        return ResponseEntity.ok("Logic to return sit-up analytics for the user");
    }
}

