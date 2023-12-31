package com.SCU.pose.repository;

import com.SCU.pose.model.PushUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PushUpRepository extends JpaRepository<PushUp, Integer> {

    // Find all PushUp records for a given user by their user ID
    List<PushUp> findByUserId(int userId);

    // Find the most recent PushUp record for a given user, ordered by the exercise date in descending order
    // Returns an Optional, which can be empty if no record is found
    List<PushUp> findByUserIdOrderByExerciseDateAscVideoIdAsc(int userId);

    Optional<PushUp> findTopByVideoIdOrderByExerciseDateDesc(int videoId);
}
